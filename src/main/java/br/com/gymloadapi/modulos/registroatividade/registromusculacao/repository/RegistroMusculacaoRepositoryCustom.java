package br.com.gymloadapi.modulos.registroatividade.registromusculacao.repository;

import br.com.gymloadapi.modulos.registroatividade.registromusculacao.model.RegistroMusculacao;

import java.util.List;
import java.util.Optional;

public interface RegistroMusculacaoRepositoryCustom {

    List<RegistroMusculacao> findAllByExercicioIdAndUsuarioId(Integer exercicioId, Integer usuarioId);

    Optional<RegistroMusculacao> findLastByExercicioIdAndUsuarioId(Integer exercicioId, Integer usuarioId);
}
