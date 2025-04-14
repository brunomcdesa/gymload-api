package br.com.gymloadapi.modulos.exercicio.model;

import org.junit.jupiter.api.Test;

import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioMusculacao;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExercicioTest {

    @Test
    void getNomeComTipoEquipamento_deveRetornarNomeComTipoExercicioCorreto_quandoSolicitado() {
        assertEquals("SUPINO RETO (HALTER)", umExercicioMusculacao(1).getNomeComTipoEquipamento());
    }
}
