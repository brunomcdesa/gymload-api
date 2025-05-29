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
        var registroAerobico = repository.findAllByExercicioIdAndUsuarioId(1, 1);

        assertAll(
            () -> assertEquals(1, registroAerobico.getFirst().getId()),
            () -> assertEquals(22.5, registroAerobico.getFirst().getDuracao()),
            () -> assertEquals(1.5, registroAerobico.getFirst().getDistancia()),
            () -> assertEquals(1, registroAerobico.getFirst().getExercicio().getId()),
            () -> assertEquals(1, registroAerobico.getFirst().getUsuario().getId())
        );
    }

    @ParameterizedTest
    @SuppressWarnings("LineLength")
    @CsvSource(value = {"9999,1", "1,999999"})
    void findAllByExercicioIdAndUsuarioId_deveRetornarListaVazia_quandoNaoEncontrarRegistrosParaOExercicioOuParaOUsuario(Integer exercicioId, Integer usuarioId) {
        assertTrue(repository.findAllByExercicioIdAndUsuarioId(exercicioId, usuarioId).isEmpty());
    }
}
