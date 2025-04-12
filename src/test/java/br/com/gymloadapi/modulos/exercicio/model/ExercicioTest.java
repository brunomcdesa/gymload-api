package br.com.gymloadapi.modulos.exercicio.model;

import org.junit.jupiter.api.Test;

import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicio;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExercicioTest {

    @Test
    void getNomeComTipoEquipamento_deveRetornarNomeComTipoExercicioCorreto_quandoSolicitado() {
        assertEquals("SUPINO RETO (HALTER)", umExercicio(1).getNomeComTipoEquipamento());
    }
}
