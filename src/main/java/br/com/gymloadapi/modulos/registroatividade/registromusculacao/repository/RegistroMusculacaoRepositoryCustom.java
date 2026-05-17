package br.com.gymloadapi.modulos.registroatividade.registromusculacao.repository;

import br.com.gymloadapi.modulos.registroatividade.registromusculacao.model.RegistroMusculacao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RegistroMusculacaoRepositoryCustom {

    List<RegistroMusculacao> findAllByExercicioIdAndUsuarioId(Integer exercicioId, Integer usuarioId);

    List<RegistroMusculacao> findAllByExercicioIdAndVariacaoIdAndUsuarioId(Integer exercicioId, Integer variacaoId,
                                                                           Integer usuarioId);

    Optional<RegistroMusculacao> findLastByExercicioIdAndUsuarioId(Integer exercicioId, Integer usuarioId);

    boolean existeRegistroHojeParaExercicios(List<Integer> exercicioIds, Integer usuarioId, LocalDate hoje);

    void migrarRegistrosSemVariacao(Integer exercicioId, Integer variacaoPadraoId);

    void moverRegistros(List<Integer> registroIds, Integer variacaoDestinoId, Integer usuarioId);
}
