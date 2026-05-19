package br.com.gymloadapi.modulos.registroatividade.registroaerobico.repository;

import br.com.gymloadapi.modulos.registroatividade.registroaerobico.model.RegistroAerobico;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RegistroAerobicoRepositoryCustom {

    List<RegistroAerobico> findAllByExercicioIdAndUsuarioId(Integer exercicioId, Integer usuarioId);

    List<RegistroAerobico> findAllByExercicioIdAndUsuarioIdIncluindoVariacoes(Integer exercicioId, Integer usuarioId);

    List<RegistroAerobico> findAllByExercicioIdAndVariacaoIdAndUsuarioId(Integer exercicioId, Integer variacaoId,
                                                                         Integer usuarioId);

    Optional<RegistroAerobico> findLastByExercicioIdAndUsuarioId(Integer exercicioId, Integer usuarioId);

    boolean existeRegistroHojeParaExercicios(List<Integer> exercicioIds, Integer usuarioId, LocalDate hoje);

    void migrarRegistrosSemVariacao(Integer exercicioId, Integer variacaoPadraoId);

    void moverRegistros(List<Integer> registroIds, Integer variacaoDestinoId, Integer usuarioId);

    List<RegistroAerobico> findTodosPrsPorUsuarioId(Integer usuarioId);
}
