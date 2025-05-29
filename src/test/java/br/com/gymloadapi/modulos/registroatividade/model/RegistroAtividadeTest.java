package br.com.gymloadapi.modulos.registroatividade.model;

import org.junit.jupiter.api.Test;

import static br.com.gymloadapi.modulos.registroatividade.registromusculacao.helper.RegistroMusculacaoHelper.umRegistroMusculacao;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RegistroAtividadeTest {

    @Test
    void getUsuarioId_deveRetornarIdDoUsuario_quandoSolicitado() {
        assertEquals(1, umRegistroMusculacao().getUsuarioId());
    }
}
