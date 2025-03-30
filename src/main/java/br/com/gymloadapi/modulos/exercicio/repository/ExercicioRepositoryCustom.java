package br.com.gymloadapi.modulos.exercicio.repository;

import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import com.querydsl.core.types.Predicate;

import java.util.List;
import java.util.UUID;

public interface ExercicioRepositoryCustom {

    List<Exercicio> findAllByPredicate(Predicate predicate);

    List<Exercicio> buscarExerciciosPorTreinoAndUsuario(Integer treinoId, UUID usuarioId);
}
