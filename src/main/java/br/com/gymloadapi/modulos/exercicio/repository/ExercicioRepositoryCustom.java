package br.com.gymloadapi.modulos.exercicio.repository;

import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import com.querydsl.core.types.Predicate;

import java.util.List;

public interface ExercicioRepositoryCustom {

    List<Exercicio> findAllComplete();

    List<Exercicio> findAllCompleteByPredicate(Predicate predicate);

    List<Exercicio> buscarExerciciosPorTreino(Integer treinoId);
}
