package br.com.gymloadapi.modulos.registroatividade.registrocardio.model;

import org.junit.jupiter.api.Test;

import static br.com.gymloadapi.modulos.registroatividade.registrocardio.helper.RegistroCardioHelper.umRegistroCardio;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RegistroCardioTest {

    @Test
    void getDistanciaFormatada_deveRetornarDistanciaFormatada_quandoSolicitado() {
        assertEquals("22,60 km", umRegistroCardio().getDistanciaFormatada());
    }

    @Test
    void getVelocidadeMedia_deveRetornarVelocidadeMedia_quandoSolicitado() {
        assertEquals("16,99 km/h", umRegistroCardio().getVelocidadeMedia());
    }
}
