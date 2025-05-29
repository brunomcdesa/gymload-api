package br.com.gymloadapi.modulos.registroatividade.registroaerobico.repository;

import br.com.gymloadapi.modulos.registroatividade.registroaerobico.model.RegistroAerobico;

import java.util.List;

public interface RegistroAerobicoRepositoryCustom {

    List<RegistroAerobico> findAllByExercicioIdAndUsuarioId(Integer exercicioId, Integer usuarioId);
}
