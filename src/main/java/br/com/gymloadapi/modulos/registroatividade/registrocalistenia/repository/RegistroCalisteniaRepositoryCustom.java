package br.com.gymloadapi.modulos.registroatividade.registrocalistenia.repository;

import br.com.gymloadapi.modulos.registroatividade.registrocalistenia.model.RegistroCalistenia;

import java.util.List;
import java.util.Optional;

public interface RegistroCalisteniaRepositoryCustom {

    List<RegistroCalistenia> findAllByExercicioIdAndUsuarioId(Integer exercicioId, Integer usuarioId);

    Optional<RegistroCalistenia> findLastByExercicioIdAndUsuarioId(Integer exercicioId, Integer usuarioId);

}
