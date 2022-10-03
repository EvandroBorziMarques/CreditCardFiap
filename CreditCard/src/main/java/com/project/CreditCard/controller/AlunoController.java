package com.project.CreditCard.controller;

import java.util.List;

import com.project.CreditCard.entity.Alunos;
import com.project.CreditCard.repository.AlunosRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class AlunoController {
    AlunosRepository repository;

    @GetMapping("/aluno")
    public List<Alunos> getAlunosList(){
        return repository.findAll();
    }

    @GetMapping("/aluno/{id}")
    public Alunos getAluno(@PathVariable Long id){
        return repository.findById(id).get();
    }

    @PostMapping("/aluno")
    public Alunos saveAlunos(@RequestBody Alunos alunos){
        return repository.save(alunos);
    }

    @DeleteMapping("/aluno/{id}")
    public void deleteAluno(@PathVariable Long id){
        repository.deleteById(id);
    }
}
