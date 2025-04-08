package br.com.gymloadapi.modulos.historicocargas;

import br.com.gymloadapi.modulos.historicocargas.repository.HistoricoCargasRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class HistoricoCargasRepositoryTest {

    @Autowired
    private HistoricoCargasRepository repository;

    @Test
    void findAllByExercicioIdAndUsuarioId_deveRetornarCargas_quandoEncontrarCargasParaOExercicioEUsuario() {
        var historicoCargas = repository.findAllByExercicioIdAndUsuarioId(1,
            UUID.fromString("c2d83d78-e1b2-4f7f-b79d-1b83f3c435f9"));

        assertAll(
            () -> assertEquals(1, historicoCargas.getFirst().getId()),
            () -> assertEquals(22.5, historicoCargas.getFirst().getCarga()),
            () -> assertEquals(12, historicoCargas.getFirst().getQtdRepeticoes()),
            () -> assertEquals(4, historicoCargas.getFirst().getQtdSeries()),
            () -> assertEquals(1, historicoCargas.getFirst().getExercicio().getId()),
            () -> assertEquals("c2d83d78-e1b2-4f7f-b79d-1b83f3c435f9", historicoCargas.getFirst().getUsuario().getId().toString())
        );
    }

    @ParameterizedTest
    @SuppressWarnings("LineLength")
    @CsvSource(value = {"9999,c2d83d78-e1b2-4f7f-b79d-1b83f3c435f9", "1,fba367b8-df4b-496b-ac23-0b3bed6ad3b4"})
    void findAllByExercicioIdAndUsuarioId_deveRetornarListaVazia_quandoEncontrarCargasParaOExercicioOuParaOUsuario(Integer exercicioId, String usuarioId) {
        assertTrue(repository.findAllByExercicioIdAndUsuarioId(exercicioId, UUID.fromString(usuarioId)).isEmpty());
    }
}
