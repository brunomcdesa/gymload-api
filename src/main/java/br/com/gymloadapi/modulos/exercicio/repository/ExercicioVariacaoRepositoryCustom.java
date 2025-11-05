package br.com.gymloadapi.modulos.exercicio.repository;

import br.com.gymloadapi.modulos.exercicio.model.ExercicioVariacao;

import java.util.List;
import java.util.Optional;

public interface ExercicioVariacaoRepositoryCustom {

    List<ExercicioVariacao> findAllByExercicioId(Integer exercicioId);

    Optional<ExercicioVariacao> findCompleteById(Integer id);
}
