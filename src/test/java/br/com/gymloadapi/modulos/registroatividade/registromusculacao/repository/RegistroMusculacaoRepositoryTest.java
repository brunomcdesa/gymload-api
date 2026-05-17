package br.com.gymloadapi.modulos.registroatividade.registromusculacao.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RegistroMusculacaoRepositoryTest {

    @Autowired
    private RegistroMusculacaoRepository repository;

    @Test
    void findAllByExercicioIdAndUsuarioId_deveRetornarRegistrosDeMusculacao_quandoEncontrarRegistrosParaOExercicioEUsuario() {
        var registrosMusculacao = repository.findAllByExercicioIdAndUsuarioId(1, 1);

        assertAll(
            () -> assertEquals(1, registrosMusculacao.getFirst().getId()),
            () -> assertEquals(22.5, registrosMusculacao.getFirst().getPeso()),
            () -> assertEquals(12, registrosMusculacao.getFirst().getQtdRepeticoes()),
            () -> assertEquals(4, registrosMusculacao.getFirst().getQtdSeries()),
            () -> assertEquals(1, registrosMusculacao.getFirst().getExercicio().getId()),
            () -> assertEquals(1, registrosMusculacao.getFirst().getUsuario().getId()),

            () -> assertEquals(2, registrosMusculacao.getLast().getId()),
            () -> assertEquals(25.0, registrosMusculacao.getLast().getPeso()),
            () -> assertEquals(8, registrosMusculacao.getLast().getQtdRepeticoes()),
            () -> assertEquals(3, registrosMusculacao.getLast().getQtdSeries()),
            () -> assertEquals(1, registrosMusculacao.getLast().getExercicio().getId()),
            () -> assertEquals(1, registrosMusculacao.getLast().getUsuario().getId())
        );
    }

    @ParameterizedTest
    @SuppressWarnings("LineLength")
    @CsvSource(value = {"9999,1", "1,999999"})
    void findAllByExercicioIdAndUsuarioId_deveRetornarListaVazia_quandoNaoEncontrarRegistrosParaOExercicioOuParaOUsuario(Integer exercicioId, Integer usuarioId) {
        assertTrue(repository.findAllByExercicioIdAndUsuarioId(exercicioId, usuarioId).isEmpty());
    }

    @Test
    void findLastByExercicioIdAndUsuarioId_deveRetornarUltimoRegistro_quandoEncontrarRegistrosParaOExercicioEParaOUsuario() {
        var ultimoRegistro = repository.findLastByExercicioIdAndUsuarioId(1, 1).get();

        assertAll(
            () -> assertEquals(4, ultimoRegistro.getId()),
            () -> assertEquals(30.0, ultimoRegistro.getPeso()),
            () -> assertEquals(8, ultimoRegistro.getQtdRepeticoes()),
            () -> assertEquals(4, ultimoRegistro.getQtdSeries()),
            () -> assertEquals(1, ultimoRegistro.getExercicio().getId()),
            () -> assertEquals(1, ultimoRegistro.getUsuario().getId())
        );
    }

    @ParameterizedTest
    @SuppressWarnings("LineLength")
    @CsvSource(value = {"9999,1", "1,999999"})
    void findLastByExercicioIdAndUsuarioId_deveRetornarOptionalVazio_quandoNaoEncontrarNenhumRegistroParaOExercicioOuParaOUsuario(Integer exercicioId, Integer usuarioId) {
        assertTrue(repository.findLastByExercicioIdAndUsuarioId(exercicioId, usuarioId).isEmpty());
    }

    @Test
    @SuppressWarnings("LineLength")
    void findAllByExercicioIdAndVariacaoIdAndUsuarioId_deveRetornarRegistros_quandoEncontrarRegistrosParaExercicioVariacaoEUsuario() {
        var registros = repository.findAllByExercicioIdAndVariacaoIdAndUsuarioId(1, 1, 1);

        assertAll(
            () -> assertEquals(1, registros.size()),
            () -> assertEquals(3, registros.getFirst().getId()),
            () -> assertEquals(20.0, registros.getFirst().getPeso()),
            () -> assertEquals(1, registros.getFirst().getExercicio().getId()),
            () -> assertEquals(1, registros.getFirst().getUsuario().getId())
        );
    }

    @ParameterizedTest
    @SuppressWarnings("LineLength")
    @CsvSource(value = {"9999,1,1", "1,9999,1", "1,1,9999"})
    void findAllByExercicioIdAndVariacaoIdAndUsuarioId_deveRetornarListaVazia_quandoNaoEncontrarRegistros(Integer exercicioId, Integer variacaoId, Integer usuarioId) {
        assertTrue(repository.findAllByExercicioIdAndVariacaoIdAndUsuarioId(exercicioId, variacaoId, usuarioId).isEmpty());
    }

    @Test
    void existeRegistroHojeParaExercicios_deveRetornarTrue_quandoExistirRegistroNaDataInformada() {
        assertTrue(repository.existeRegistroHojeParaExercicios(List.of(1), 1, LocalDate.of(2025, 3, 4)));
    }

    @Test
    void existeRegistroHojeParaExercicios_deveRetornarFalse_quandoNaoExistirRegistroNaDataInformada() {
        assertFalse(repository.existeRegistroHojeParaExercicios(List.of(1), 1, LocalDate.now()));
    }

    @Test
    void migrarRegistrosSemVariacao_deveMigrarRegistrosSemVariacaoParaVariacaoPadrao_quandoSolicitado() {
        repository.migrarRegistrosSemVariacao(1, 3);

        var registrosSemVariacao = repository.findAllByExercicioIdAndUsuarioId(1, 1);
        var registrosComVariacao3 = repository.findAllByExercicioIdAndVariacaoIdAndUsuarioId(1, 3, 1);

        assertAll(
            () -> assertTrue(registrosSemVariacao.isEmpty()),
            () -> assertEquals(2, registrosComVariacao3.size())
        );
    }

    @Test
    void moverRegistros_deveMoverRegistrosParaVariacaoDestino_quandoSolicitado() {
        repository.moverRegistros(List.of(3), 2, 1);

        var registrosVariacao2 = repository.findAllByExercicioIdAndVariacaoIdAndUsuarioId(1, 2, 1);

        assertAll(
            () -> assertEquals(2, registrosVariacao2.size()),
            () -> assertTrue(registrosVariacao2.stream().anyMatch(r -> r.getId().equals(3)))
        );
    }

    @Test
    void findTodosPrsPorUsuarioId_deveRetornarTodosOsRegistrosDoUsuario_quandoEncontrarRegistros() {
        var registros = repository.findTodosPrsPorUsuarioId(1);

        assertAll(
            () -> assertEquals(4, registros.size()),
            () -> assertTrue(registros.stream().allMatch(r -> r.getUsuario().getId().equals(1)))
        );
    }

    @Test
    void findTodosPrsPorUsuarioId_deveRetornarListaVazia_quandoNaoEncontrarRegistrosParaOUsuario() {
        assertTrue(repository.findTodosPrsPorUsuarioId(999999).isEmpty());
    }
}
