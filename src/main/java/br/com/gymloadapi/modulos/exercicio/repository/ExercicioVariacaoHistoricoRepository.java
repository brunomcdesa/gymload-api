package br.com.gymloadapi.modulos.exercicio.repository;

import br.com.gymloadapi.modulos.exercicio.model.ExercicioVariacaoHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ExercicioVariacaoHistoricoRepository extends JpaRepository<ExercicioVariacaoHistorico, Integer>,
    QuerydslPredicateExecutor<ExercicioVariacaoHistorico> {
}
