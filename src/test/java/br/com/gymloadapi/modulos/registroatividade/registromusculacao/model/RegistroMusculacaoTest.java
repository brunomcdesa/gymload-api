package br.com.gymloadapi.modulos.registroatividade.registromusculacao.model;

import org.junit.jupiter.api.Test;

import static br.com.gymloadapi.modulos.registroatividade.registromusculacao.helper.RegistroMusculacaoHelper.umRegistroMusculacao;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RegistroMusculacaoTest {

    @Test
    void getPesoComUnidadePeso_deveRetornarPesoComUnidadePeso_quandoSolicitado() {
        assertEquals("22.5 (KG)", umRegistroMusculacao().getPesoComUnidadePeso());
    }
}
