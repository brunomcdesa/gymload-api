package br.com.gymloadapi.modulos.registroatividade.registrocardio.repository;

import br.com.gymloadapi.modulos.registroatividade.registrocardio.model.RegistroCardio;

import java.util.List;

public interface RegistroCardioRepositoryCustom {

    List<RegistroCardio> findAllByExercicioIdAndUsuarioId(Integer exercicioId, Integer usuarioId);
}
