package br.com.gymloadapi.modulos.registroatividade.registrocardio.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RegistroCardioRepositoryTest {

    @Autowired
    private RegistroCardioRepository repository;

    @Test
    void findAllByExercicioIdAndUsuarioId_deveRetornarRegistroCardio_quandoExercicioPossuirRegistro() {
        var registroCardio = repository.findAllByExercicioIdAndUsuarioId(1, 1);

        assertAll(
            () -> assertEquals(1, registroCardio.getFirst().getId()),
            () -> assertEquals(22.5, registroCardio.getFirst().getDuracao()),
            () -> assertEquals(1.5, registroCardio.getFirst().getDistancia()),
            () -> assertEquals(1, registroCardio.getFirst().getExercicio().getId()),
            () -> assertEquals(1, registroCardio.getFirst().getUsuario().getId())
        );
    }

    @ParameterizedTest
    @SuppressWarnings("LineLength")
    @CsvSource(value = {"9999,1", "1,999999"})
    void findAllByExercicioIdAndUsuarioId_deveRetornarListaVazia_quandoNaoEncontrarCargasParaOExercicioOuParaOUsuario(Integer exercicioId, Integer usuarioId) {
        assertTrue(repository.findAllByExercicioIdAndUsuarioId(exercicioId, usuarioId).isEmpty());
    }
}
