package com.project.CreditCard.controller;

import com.project.CreditCard.dto.TransacaoDTO;
import com.project.CreditCard.entity.Alunos;
import com.project.CreditCard.entity.Transacao;
import com.project.CreditCard.repository.AlunosRepository;
import com.project.CreditCard.repository.TransacaoRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@RestController
@AllArgsConstructor
public class TransacaoController {
    TransacaoRepository transacaoRepository;
    AlunosRepository alunosRepository;

    @GetMapping("/transacao")
    public List<Transacao> getTransacaoList(){
        return transacaoRepository.findAll();
    }

    @PostMapping("/transacao")
    public Transacao saveTransacao(@RequestBody TransacaoDTO transacaoRequest){
        Transacao transacao = new Transacao();
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d/MM/uuuu");
        String text = date.format(formatters);
        LocalDate parsedDate = LocalDate.parse(text, formatters);
        transacao.setDate(parsedDate);



        Alunos alunos = alunosRepository.findByCpfAndRm(transacaoRequest.getCpf(), transacaoRequest.getRm());

        return transacaoRepository.save(transacao);
    }

    @GetMapping("/transacao/{id}")
    public Transacao getTransacao(@PathVariable Long id){
        return transacaoRepository.findById(id).get();
    }
}
