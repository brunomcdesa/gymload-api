package br.com.gymloadapi.modulos.gradesemanal.dto;

import org.junit.jupiter.api.Test;

import static br.com.gymloadapi.modulos.comum.enums.EDiaSemana.SEGUNDA;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GradeSemanalResponseTest {

    @Test
    void response_deveExporOsCamposInformados_quandoConstruido() {
        var response = new GradeSemanalResponse(SEGUNDA, 1, "Um Treino");

        assertAll(
            () -> assertEquals(SEGUNDA, response.diaSemana()),
            () -> assertEquals(1, response.treinoId()),
            () -> assertEquals("Um Treino", response.treinoNome())
        );
    }
}
