package br.com.gymloadapi.modulos.registroatividade.registrocarga.repository;

import br.com.gymloadapi.modulos.registroatividade.registrocarga.repository.RegistroCargaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RegistroCargaRepositoryTest {

    @Autowired
    private RegistroCargaRepository repository;

    @Test
    void findAllByExercicioIdAndUsuarioId_deveRetornarCargas_quandoEncontrarCargasParaOExercicioEUsuario() {
        var historicoCargas = repository.findAllByExercicioIdAndUsuarioId(1, 1);

        assertAll(
            () -> assertEquals(1, historicoCargas.getFirst().getId()),
            () -> assertEquals(22.5, historicoCargas.getFirst().getPeso()),
            () -> assertEquals(12, historicoCargas.getFirst().getQtdRepeticoes()),
            () -> assertEquals(4, historicoCargas.getFirst().getQtdSeries()),
            () -> assertEquals(1, historicoCargas.getFirst().getExercicio().getId()),
            () -> assertEquals(1, historicoCargas.getFirst().getUsuario().getId())
        );
    }

    @ParameterizedTest
    @SuppressWarnings("LineLength")
    @CsvSource(value = {"9999,1", "1,999999"})
    void findAllByExercicioIdAndUsuarioId_deveRetornarListaVazia_quandoNaoEncontrarCargasParaOExercicioOuParaOUsuario(Integer exercicioId, Integer usuarioId) {
        assertTrue(repository.findAllByExercicioIdAndUsuarioId(exercicioId, usuarioId).isEmpty());
    }
}
