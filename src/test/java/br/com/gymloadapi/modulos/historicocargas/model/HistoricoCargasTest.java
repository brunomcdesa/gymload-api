package br.com.gymloadapi.modulos.historicocargas.model;

import org.junit.jupiter.api.Test;

import static br.com.gymloadapi.modulos.historicocargas.helper.HistoricoCargasHelper.umHistoricoCargas;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoricoCargasTest {

    @Test
    void getCargaComUnidadePeso_deveRetornarCargaComUnidadePeso_quandoSolicitado() {
        assertEquals("22.5 (KG)", umHistoricoCargas().getCargaComUnidadePeso());
    }
}
