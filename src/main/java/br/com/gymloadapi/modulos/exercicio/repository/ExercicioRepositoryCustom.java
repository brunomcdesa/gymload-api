package br.com.gymloadapi.modulos.exercicio.repository;

import br.com.gymloadapi.modulos.exercicio.model.Exercicio;

import java.util.List;

public interface ExercicioRepositoryCustom {

    List<Exercicio> findAllComplete();

    List<Exercicio> buscarExerciciosPorTreino(Integer treinoId);
}
