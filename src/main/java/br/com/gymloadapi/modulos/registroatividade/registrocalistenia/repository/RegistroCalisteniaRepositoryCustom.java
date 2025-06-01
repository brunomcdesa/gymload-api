package br.com.gymloadapi.modulos.registroatividade.registrocalistenia.repository;

import br.com.gymloadapi.modulos.registroatividade.registrocalistenia.model.RegistroCalistenia;

import java.util.List;

public interface RegistroCalisteniaRepositoryCustom {

    List<RegistroCalistenia> findAllByExercicioIdAndUsuarioId(Integer exercicioId, Integer usuarioId);
}
