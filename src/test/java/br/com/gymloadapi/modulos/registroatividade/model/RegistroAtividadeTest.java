package br.com.gymloadapi.modulos.registroatividade.model;

import org.junit.jupiter.api.Test;

import static br.com.gymloadapi.modulos.registroatividade.registrocarga.helper.RegistroCargaHelper.umRegistroCarga;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RegistroAtividadeTest {

    @Test
    void getUsuarioId_deveRetornarIdDoUsuario_quandoSolicitado() {
        assertEquals(1, umRegistroCarga().getUsuarioId());
    }
}
