package com.project.CreditCard.repository;

import com.project.CreditCard.entity.Alunos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlunosRepository extends JpaRepository<Alunos, Long> {

    Alunos findByCpfAndRm(String cpf, String rm);

}
