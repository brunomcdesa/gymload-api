package br.com.gymloadapi.modulos.historicocargas.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class HistoricoCargasRepositoryTest {

    @Autowired
    private HistoricoCargasRepository repository;

    @Test
    void findAllByExercicioIdAndUsuarioId_deveRetornarCargas_quandoEncontrarCargasParaOExercicioEUsuario() {
        var historicoCargas = repository.findAllByExercicioIdAndUsuarioId(1, 1);

        assertAll(
            () -> assertEquals(1, historicoCargas.getFirst().getId()),
            () -> assertEquals(22.5, historicoCargas.getFirst().getCarga()),
            () -> assertEquals(12, historicoCargas.getFirst().getQtdRepeticoes()),
            () -> assertEquals(4, historicoCargas.getFirst().getQtdSeries()),
            () -> assertEquals(1, historicoCargas.getFirst().getExercicio().getId()),
            () -> assertEquals(1, historicoCargas.getFirst().getUsuario().getId())
        );
    }

    @ParameterizedTest
    @SuppressWarnings("LineLength")
    @CsvSource(value = {"9999,1", "1,999999"})
    void findAllByExercicioIdAndUsuarioId_deveRetornarListaVazia_quandoEncontrarCargasParaOExercicioOuParaOUsuario(Integer exercicioId, Integer usuarioId) {
        assertTrue(repository.findAllByExercicioIdAndUsuarioId(exercicioId, usuarioId).isEmpty());
    }
}
