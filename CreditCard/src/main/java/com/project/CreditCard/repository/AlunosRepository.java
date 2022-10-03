package com.project.CreditCard.repository;

import com.project.CreditCard.entity.Alunos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunosRepository extends JpaRepository<Alunos, Long> {

    Alunos findByCpfAndRm(String cpf, String rm);

}
