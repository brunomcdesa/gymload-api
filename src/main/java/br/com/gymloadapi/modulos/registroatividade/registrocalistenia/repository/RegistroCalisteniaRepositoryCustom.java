package br.com.gymloadapi.modulos.registroatividade.registrocalistenia.repository;

import br.com.gymloadapi.modulos.registroatividade.registrocalistenia.model.RegistroCalistenia;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RegistroCalisteniaRepositoryCustom {

    List<RegistroCalistenia> findAllByExercicioIdAndUsuarioId(Integer exercicioId, Integer usuarioId);

    List<RegistroCalistenia> findAllByExercicioIdAndUsuarioIdIncluindoVariacoes(Integer exercicioId, Integer usuarioId);

    List<RegistroCalistenia> findAllByExercicioIdAndVariacaoIdAndUsuarioId(Integer exercicioId, Integer variacaoId,
                                                                           Integer usuarioId);

    Optional<RegistroCalistenia> findLastByExercicioIdAndUsuarioId(Integer exercicioId, Integer usuarioId);

    boolean existeRegistroHojeParaExercicios(List<Integer> exercicioIds, Integer usuarioId, LocalDate hoje);

    void migrarRegistrosSemVariacao(Integer exercicioId, Integer variacaoPadraoId);

    void moverRegistros(List<Integer> registroIds, Integer variacaoDestinoId, Integer usuarioId);

    List<RegistroCalistenia> findTodosPrsPorUsuarioId(Integer usuarioId);
}
