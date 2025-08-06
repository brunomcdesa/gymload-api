package br.com.gymloadapi.modulos.exercicio.repository;

import br.com.gymloadapi.modulos.exercicio.model.ExercicioVariacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExercicioVariacaoRepository extends JpaRepository<ExercicioVariacao, Integer>,
    QuerydslPredicateExecutor<ExercicioVariacao> {

    List<ExercicioVariacao> findAllByExercicio_Id(Integer exercicioId);
}
