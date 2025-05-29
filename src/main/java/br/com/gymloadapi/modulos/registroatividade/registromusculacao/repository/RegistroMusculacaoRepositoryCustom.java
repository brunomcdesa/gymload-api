package br.com.gymloadapi.modulos.registroatividade.registromusculacao.repository;

import br.com.gymloadapi.modulos.registroatividade.registromusculacao.model.RegistroMusculacao;

import java.util.List;

public interface RegistroMusculacaoRepositoryCustom {

    List<RegistroMusculacao> findAllByExercicioIdAndUsuarioId(Integer exercicioId, Integer usuarioId);
}
