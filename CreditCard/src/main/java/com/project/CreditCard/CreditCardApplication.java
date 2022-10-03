package com.project.CreditCard;

import com.project.CreditCard.entity.Alunos;
import com.project.CreditCard.util.GeradorCPF;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@EnableWebMvc
@SpringBootApplication
@EnableSwagger2
@EnableBatchProcessing
@Slf4j
public class CreditCardApplication {
	public static void main(String[] args) {
		SpringApplication.run(CreditCardApplication.class, args);
	}


	@Bean
	public String trataFileTxt(@Value("${input.file}") Resource arquivoRaw, @Value("${input.file.clean}") String filePath) throws IOException {
		File arquivoTratado = new File(filePath + "/lista_alunos_tratada.txt");
		FileUtils.write(arquivoTratado, "", StandardCharsets.UTF_8);
		List<String> lines = FileUtils.readLines(arquivoRaw.getFile(), StandardCharsets.UTF_8);
		lines.forEach(line -> {
			try {
				if (!(line.contains("--") || line.isEmpty() || line.isBlank())) {
					FileUtils.writeStringToFile(arquivoTratado, line.replaceAll("\\s{2,}", ";")+"\n", StandardCharsets.UTF_8, true) ;
				}
				;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		log.info("Tratou");
		return "Tratou!!!";
	}

	@Bean
	public FlatFileItemReader<Alunos> fileReader(@Value("${input.file.target}") Resource resource) {
		return new FlatFileItemReaderBuilder<Alunos>()
				.name("read file")
				.resource(resource)
				.targetType(Alunos.class)
				.delimited().delimiter(";").names("name", "rm")
				.build();
	}

	@Bean
	public ItemProcessor<Alunos, Alunos> processor() {
		GeradorCPF cpf = new GeradorCPF();
		return aluno -> {
			aluno.setName(aluno.getName().toUpperCase());
			aluno.setCpf(cpf.cpf(false));
			aluno.setRm(aluno.getRm().replaceAll("\\.+", "").replace("-", "").trim());
			return aluno;
		};
	}

	@Bean
	public JdbcBatchItemWriter databaseWriter(DataSource datasource) {
		return new JdbcBatchItemWriterBuilder<Alunos>()
				.dataSource(datasource)
				.sql("insert into TB_ALUNO (NAME, RM, cpf) VALUES (:name, :rm, :cpf)")
				.beanMapped()
				.build();
	}

	@Bean
	public Step step(StepBuilderFactory stepBuilderFactory,
					 ItemReader<Alunos> itemReader,
					 ItemWriter<Alunos> itemWriter,
					 ItemProcessor<Alunos, Alunos> processor) {
		return stepBuilderFactory.get("Step file to DB")
				.<Alunos, Alunos>chunk(2)
				.reader(itemReader)
				.processor(processor)
				.writer(itemWriter)
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Job job(JobBuilderFactory jobBuilderFactory,
				   Step step) {
		return jobBuilderFactory.get("alunoJob")
				.start(step)
				.build();
	}

}
