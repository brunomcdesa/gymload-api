package br.com.gymloadapi.modulos.registroatividade.registromusculacao.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

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
            () -> assertEquals(2, ultimoRegistro.getId()),
            () -> assertEquals(25.0, ultimoRegistro.getPeso()),
            () -> assertEquals(8, ultimoRegistro.getQtdRepeticoes()),
            () -> assertEquals(3, ultimoRegistro.getQtdSeries()),
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
}
