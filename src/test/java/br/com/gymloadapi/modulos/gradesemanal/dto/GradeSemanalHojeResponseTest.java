package br.com.gymloadapi.modulos.gradesemanal.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GradeSemanalHojeResponseTest {

    @Test
    void response_deveExporOsCamposInformados_quandoConstruido() {
        var response = new GradeSemanalHojeResponse(1, "Um Treino", true);

        assertAll(
            () -> assertEquals(1, response.treinoId()),
            () -> assertEquals("Um Treino", response.treinoNome()),
            () -> assertTrue(response.concluidoHoje())
        );
    }

    @Test
    void response_deveAceitarConcluidoHojeFalse_quandoSolicitado() {
        var response = new GradeSemanalHojeResponse(1, "Um Treino", false);
        assertFalse(response.concluidoHoje());
    }
}
