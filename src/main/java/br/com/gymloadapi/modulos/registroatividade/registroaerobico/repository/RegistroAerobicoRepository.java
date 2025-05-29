package br.com.gymloadapi.modulos.registroatividade.registroaerobico.repository;

import br.com.gymloadapi.modulos.registroatividade.registroaerobico.model.RegistroAerobico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroAerobicoRepository extends JpaRepository<RegistroAerobico, Integer>,
    QuerydslPredicateExecutor<RegistroAerobico>, RegistroAerobicoRepositoryCustom {
}
