package br.com.gymloadapi.modulos.registroatividade.registroaerobico.model;

import org.junit.jupiter.api.Test;

import static br.com.gymloadapi.modulos.registroatividade.registroaerobico.helper.RegistroMusculacaoHelper.umRegistroAerobico;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RegistroAerobicoTest {

    @Test
    void getDistanciaFormatada_deveRetornarDistanciaFormatada_quandoSolicitado() {
        assertEquals("22.6 km", umRegistroAerobico().getDistanciaFormatada());
    }

    @Test
    void getVelocidadeMedia_deveRetornarVelocidadeMedia_quandoSolicitado() {
        assertEquals("16.99 km/h", umRegistroAerobico().getVelocidadeMedia());
    }
}
