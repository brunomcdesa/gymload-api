package br.com.gymloadapi.modulos.registroatividade.registroaerobico.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

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
}
