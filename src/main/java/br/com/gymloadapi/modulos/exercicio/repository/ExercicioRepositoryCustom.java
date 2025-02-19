package br.com.gymloadapi.modulos.exercicio.repository;

import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import com.querydsl.core.types.Predicate;

import java.util.List;

public interface ExercicioRepositoryCustom {

    List<Exercicio> findAllByPredicate(Predicate predicate);
}
