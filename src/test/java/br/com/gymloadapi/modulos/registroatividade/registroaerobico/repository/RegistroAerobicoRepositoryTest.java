package br.com.gymloadapi.modulos.registroatividade.registroaerobico.repository;

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
class RegistroAerobicoRepositoryTest {

    @Autowired
    private RegistroAerobicoRepository repository;

    @Test
    void findAllByExercicioIdAndUsuarioId_deveRetornarRegistroAerobico_quandoExercicioPossuirRegistro() {
        var registroAerobico = repository.findAllByExercicioIdAndUsuarioId(2, 1);

        assertAll(
            () -> assertEquals(1, registroAerobico.getFirst().getId()),
            () -> assertEquals(22.5, registroAerobico.getFirst().getDuracao()),
            () -> assertEquals(1.5, registroAerobico.getFirst().getDistancia()),
            () -> assertEquals(2, registroAerobico.getFirst().getExercicio().getId()),
            () -> assertEquals(1, registroAerobico.getFirst().getUsuario().getId()),

            () -> assertEquals(2, registroAerobico.getLast().getId()),
            () -> assertEquals(20.0, registroAerobico.getLast().getDuracao()),
            () -> assertEquals(2.5, registroAerobico.getLast().getDistancia()),
            () -> assertEquals(2, registroAerobico.getLast().getExercicio().getId()),
            () -> assertEquals(1, registroAerobico.getLast().getUsuario().getId())
        );
    }

    @ParameterizedTest
    @SuppressWarnings("LineLength")
    @CsvSource(value = {"9999,1", "2,999999"})
    void findAllByExercicioIdAndUsuarioId_deveRetornarListaVazia_quandoNaoEncontrarRegistrosParaOExercicioOuParaOUsuario(Integer exercicioId, Integer usuarioId) {
        assertTrue(repository.findAllByExercicioIdAndUsuarioId(exercicioId, usuarioId).isEmpty());
    }

    @Test
    void findLastByExercicioIdAndUsuarioId_deveRetornarUltimoRegistro_quandoEncontrarRegistrosParaOExercicioEParaOUsuario() {
        var ultimoRegistro = repository.findLastByExercicioIdAndUsuarioId(2, 1).get();

        assertAll(
            () -> assertEquals(2, ultimoRegistro.getId()),
            () -> assertEquals(20.0, ultimoRegistro.getDuracao()),
            () -> assertEquals(2.5, ultimoRegistro.getDistancia()),
            () -> assertEquals(2, ultimoRegistro.getExercicio().getId()),
            () -> assertEquals(1, ultimoRegistro.getUsuario().getId())
        );
    }

    @ParameterizedTest
    @SuppressWarnings("LineLength")
    @CsvSource(value = {"9999,1", "2,999999"})
    void findLastByExercicioIdAndUsuarioId_deveRetornarOptionalVazio_quandoNaoEncontrarNenhumRegistroParaOExercicioOuParaOUsuario(Integer exercicioId, Integer usuarioId) {
        assertTrue(repository.findLastByExercicioIdAndUsuarioId(exercicioId, usuarioId).isEmpty());
    }

    @Test
    void findAllByExercicioIdAndVariacaoIdAndUsuarioId_deveRetornarListaVazia_quandoNaoExistiremRegistrosComVariacao() {
        assertTrue(repository.findAllByExercicioIdAndVariacaoIdAndUsuarioId(2, 1, 1).isEmpty());
    }

    @Test
    void existeRegistroHojeParaExercicios_deveRetornarTrue_quandoExistirRegistroNaDataInformada() {
        assertTrue(repository.existeRegistroHojeParaExercicios(List.of(2), 1, LocalDate.of(2025, 3, 4)));
    }

    @Test
    void existeRegistroHojeParaExercicios_deveRetornarFalse_quandoNaoExistirRegistroNaDataInformada() {
        assertFalse(repository.existeRegistroHojeParaExercicios(List.of(2), 1, LocalDate.now()));
    }

    @Test
    void migrarRegistrosSemVariacao_deveMigrarRegistrosSemVariacaoParaVariacaoPadrao_quandoSolicitado() {
        repository.migrarRegistrosSemVariacao(2, 1);

        var registrosSemVariacao = repository.findAllByExercicioIdAndUsuarioId(2, 1);
        var registrosComVariacao1 = repository.findAllByExercicioIdAndVariacaoIdAndUsuarioId(2, 1, 1);

        assertAll(
            () -> assertTrue(registrosSemVariacao.isEmpty()),
            () -> assertEquals(2, registrosComVariacao1.size())
        );
    }

    @Test
    void moverRegistros_deveMoverRegistrosParaVariacaoDestino_quandoSolicitado() {
        repository.moverRegistros(List.of(1), 1, 1);

        var registrosVariacao1 = repository.findAllByExercicioIdAndVariacaoIdAndUsuarioId(2, 1, 1);

        assertAll(
            () -> assertEquals(1, registrosVariacao1.size()),
            () -> assertEquals(1, registrosVariacao1.getFirst().getId())
        );
    }

    @Test
    void findTodosPrsPorUsuarioId_deveRetornarTodosOsRegistrosDoUsuario_quandoEncontrarRegistros() {
        var registros = repository.findTodosPrsPorUsuarioId(1);

        assertAll(
            () -> assertEquals(2, registros.size()),
            () -> assertTrue(registros.stream().allMatch(r -> r.getUsuario().getId().equals(1))),
            () -> assertTrue(registros.stream().allMatch(r -> r.getExercicio().getId().equals(2)))
        );
    }

    @Test
    void findTodosPrsPorUsuarioId_deveRetornarListaVazia_quandoNaoEncontrarRegistrosParaOUsuario() {
        assertTrue(repository.findTodosPrsPorUsuarioId(999999).isEmpty());
    }
}
