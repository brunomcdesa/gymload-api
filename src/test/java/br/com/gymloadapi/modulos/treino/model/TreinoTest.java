package br.com.gymloadapi.modulos.treino.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static br.com.gymloadapi.modulos.treino.helper.TreinoHelper.umTreino;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TreinoTest {

    @Test
    void getExerciciosIds_deveRetornarListaDeIdsDosExercicios_quandoSolicitado() {
        assertEquals(List.of(1, 2), umTreino().getExerciciosIds());
    }
}
