package br.com.gymloadapi.modulos.registroatividade.registrocarga.repository;

import br.com.gymloadapi.modulos.registroatividade.registrocarga.model.RegistroCarga;

import java.util.List;

public interface HistoricoCargasRepositoryCustom {

    List<RegistroCarga> findAllByExercicioIdAndUsuarioId(Integer exercicioId, Integer usuarioId);
}
