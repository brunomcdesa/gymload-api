package br.com.gymloadapi.modulos.exercicio.repository;

import br.com.gymloadapi.modulos.exercicio.model.ExercicioHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ExercicioHistoricoRepository extends JpaRepository<ExercicioHistorico, Integer>,
    QuerydslPredicateExecutor<ExercicioHistorico> {
}
