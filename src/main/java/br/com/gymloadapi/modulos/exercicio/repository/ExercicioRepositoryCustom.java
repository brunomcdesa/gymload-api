package br.com.gymloadapi.modulos.exercicio.repository;

import br.com.gymloadapi.modulos.exercicio.model.Exercicio;

import java.util.List;
import java.util.UUID;

public interface ExercicioRepositoryCustom {

    List<Exercicio> buscarExerciciosPorTreinoAndUsuario(Integer treinoId, UUID usuarioId);
}
