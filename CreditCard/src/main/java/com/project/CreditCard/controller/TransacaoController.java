package com.project.CreditCard.controller;

import com.project.CreditCard.dto.TransacaoDTO;
import com.project.CreditCard.entity.Alunos;
import com.project.CreditCard.entity.Transacao;
import com.project.CreditCard.repository.AlunosRepository;
import com.project.CreditCard.repository.TransacaoRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@AllArgsConstructor
public class TransacaoController {
    TransacaoRepository transacaoRepository;
    AlunosRepository alunosRepository;

    @GetMapping("/transacao")
    public List<Transacao> getTransacaoList(){
        return transacaoRepository.findAll();
    }

    @GetMapping("/transacao/historico")
    public void gerarArquivoCSV(HttpServletResponse response) {
        List<Transacao> transacoes = transacaoRepository.findAll();
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            List<String[]> dataLines = new ArrayList<>();
            transacoes.forEach(t -> {
                dataLines.add(new String[] {t.getAlunos().getName(), t.getDate().toString(), String.valueOf(t.getValor())});
            });

            StringBuilder sb = new StringBuilder();

            for (String[] s : dataLines)
                sb.append(convertToCSV(s));

            new DataOutputStream(byteArrayOutputStream).write(String.valueOf(sb).getBytes());
            byteArrayOutputStream.flush();

            response.setHeader("Content-Disposition", "attachment; filename=teste.csv");
            response.setContentType("text/csv");
            response.getOutputStream().write(byteArrayOutputStream.toByteArray());
            response.flushBuffer();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(";")) + "\n";
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    @PostMapping("/transacao")
    public Transacao saveTransacao(@RequestBody TransacaoDTO transacaoRequest){
        Transacao transacao = new Transacao();
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d/MM/uuuu");
        String text = date.format(formatters);
        LocalDate parsedDate = LocalDate.parse(text, formatters);
        transacao.setDate(parsedDate);
        transacao.setValor(transacaoRequest.getValor());

        Alunos alunos = alunosRepository.findByCpfAndRm(transacaoRequest.getCpf(), transacaoRequest.getRm());
        transacao.setAlunos(alunos);

        return transacaoRepository.save(transacao);
    }

    @GetMapping("/transacao/{id}")
    public Transacao getTransacao(@PathVariable Long id){
        return transacaoRepository.findById(id).get();
    }
}
