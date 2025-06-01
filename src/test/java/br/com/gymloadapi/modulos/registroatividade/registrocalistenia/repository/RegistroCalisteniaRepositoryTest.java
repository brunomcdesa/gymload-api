package br.com.gymloadapi.modulos.registroatividade.registrocalistenia.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static br.com.gymloadapi.modulos.comum.enums.EUnidadePeso.KG;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RegistroCalisteniaRepositoryTest {

    @Autowired
    private RegistroCalisteniaRepository repository;

    @Test
    void findAllByExercicioIdAndUsuarioId_deveRetornarRegistrosDeCalistenia_quandoEncontrarRegistroParaOExercicioEUsuario() {
        var registrosCalistenia = repository.findAllByExercicioIdAndUsuarioId(3, 1);

        assertAll(
            () -> assertEquals(1, registrosCalistenia.getFirst().getId()),
            () -> assertNull(registrosCalistenia.getFirst().getPesoAdicional()),
            () -> assertNull(registrosCalistenia.getFirst().getUnidadePeso()),
            () -> assertEquals(30, registrosCalistenia.getFirst().getQtdRepeticoes()),
            () -> assertEquals(5, registrosCalistenia.getFirst().getQtdSeries()),
            () -> assertEquals(3, registrosCalistenia.getFirst().getExercicio().getId()),
            () -> assertEquals(1, registrosCalistenia.getFirst().getUsuario().getId()),

            () -> assertEquals(2, registrosCalistenia.getLast().getId()),
            () -> assertEquals(10.0, registrosCalistenia.getLast().getPesoAdicional()),
            () -> assertEquals(KG, registrosCalistenia.getLast().getUnidadePeso()),
            () -> assertEquals(50, registrosCalistenia.getLast().getQtdRepeticoes()),
            () -> assertEquals(4, registrosCalistenia.getLast().getQtdSeries()),
            () -> assertEquals(3, registrosCalistenia.getLast().getExercicio().getId()),
            () -> assertEquals(1, registrosCalistenia.getLast().getUsuario().getId())
        );
    }

    @ParameterizedTest
    @SuppressWarnings("LineLength")
    @CsvSource(value = {"9999,1", "3,999999"})
    void findAllByExercicioIdAndUsuarioId_deveRetornarListaVazia_quandoNaoEncontrarRegistrosParaOExercicioOuParaOUsuario(Integer exercicioId, Integer usuarioId) {
        assertTrue(repository.findAllByExercicioIdAndUsuarioId(exercicioId, usuarioId).isEmpty());
    }

    @Test
    void findLastByExercicioIdAndUsuarioId_deveRetornarUltimoRegistro_quandoEncontrarRegistrosParaOExercicioEParaOUsuario() {
        var ultimoRegistro = repository.findLastByExercicioIdAndUsuarioId(3, 1).get();

        assertAll(
            () -> assertEquals(2, ultimoRegistro.getId()),
            () -> assertEquals(10.0, ultimoRegistro.getPesoAdicional()),
            () -> assertEquals(KG, ultimoRegistro.getUnidadePeso()),
            () -> assertEquals(50, ultimoRegistro.getQtdRepeticoes()),
            () -> assertEquals(4, ultimoRegistro.getQtdSeries()),
            () -> assertEquals(3, ultimoRegistro.getExercicio().getId()),
            () -> assertEquals(1, ultimoRegistro.getUsuario().getId())
        );
    }

    @ParameterizedTest
    @SuppressWarnings("LineLength")
    @CsvSource(value = {"9999,1", "3,999999"})
    void findLastByExercicioIdAndUsuarioId_deveRetornarOptionalVazio_quandoNaoEncontrarNenhumRegistroParaOExercicioOuParaOUsuario(Integer exercicioId, Integer usuarioId) {
        assertTrue(repository.findLastByExercicioIdAndUsuarioId(exercicioId, usuarioId).isEmpty());
    }
}
