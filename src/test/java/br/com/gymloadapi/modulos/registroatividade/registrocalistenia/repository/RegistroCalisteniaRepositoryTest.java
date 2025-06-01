package br.com.gymloadapi.modulos.registroatividade.registrocalistenia.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

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
            () -> assertEquals(30, registrosCalistenia.getFirst().getQtdRepeticoes()),
            () -> assertEquals(5, registrosCalistenia.getFirst().getQtdSeries()),
            () -> assertEquals(3, registrosCalistenia.getFirst().getExercicio().getId()),
            () -> assertEquals(1, registrosCalistenia.getFirst().getUsuario().getId())
        );
    }
}
