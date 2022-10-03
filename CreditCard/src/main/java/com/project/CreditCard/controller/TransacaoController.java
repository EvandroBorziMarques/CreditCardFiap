package com.project.CreditCard.controller;

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

    @GetMapping("/transacao")
    public List<Transacao> getTransacaoList(){
        return transacaoRepository.findAll();
    }

    @PostMapping("/transacao")
    public Transacao saveTransacao(@RequestBody Transacao transacao){
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d/MM/uuuu");
        String text = date.format(formatters);
        LocalDate parsedDate = LocalDate.parse(text, formatters);
        transacao.setDate(parsedDate);
        return transacaoRepository.save(transacao);
    }

    @GetMapping("/transacao/{id}")
    public Transacao getTrasacao(@PathVariable Long id){
        return transacaoRepository.findById(id).get();
    }
}
