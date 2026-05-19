package br.com.gymloadapi.modulos.gradesemanal.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GradeSemanalRequestTest {

    @Test
    void treinoId_deveRetornarOValorInformado_quandoConstruido() {
        var request = new GradeSemanalRequest(42);
        assertEquals(42, request.treinoId());
    }

    @Test
    void treinoId_deveAceitarNull_quandoConstruidoSemValor() {
        var request = new GradeSemanalRequest(null);
        assertNull(request.treinoId());
    }
}
