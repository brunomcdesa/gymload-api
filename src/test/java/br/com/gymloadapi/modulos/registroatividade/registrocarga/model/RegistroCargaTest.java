package br.com.gymloadapi.modulos.registroatividade.registrocarga.model;

import org.junit.jupiter.api.Test;

import static br.com.gymloadapi.modulos.registroatividade.registrocarga.helper.RegistroCargaHelper.umRegistroCarga;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RegistroCargaTest {

    @Test
    void getCargaComUnidadePeso_deveRetornarCargaComUnidadePeso_quandoSolicitado() {
        assertEquals("22.5 (KG)", umRegistroCarga().getCargaComUnidadePeso());
    }
}
