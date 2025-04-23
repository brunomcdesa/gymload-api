package br.com.gymloadapi.modulos.exercicio.model;

import org.junit.jupiter.api.Test;

import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioAerobico;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioMusculacao;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExercicioTest {

    @Test
    void getNomeComTipoEquipamento_deveRetornarNomeComTipoEquipamento_quandoSolicitado() {
        assertEquals("SUPINO RETO (HALTER)", umExercicioMusculacao(1).getNomeComTipoEquipamento());
    }

    @Test
    void getNomeComTipoEquipamento_deveRetornarNomeSemTipoEquipamento_quandoExercicioNaoPossuirTipoEquipamento() {
        assertEquals("Esteira", umExercicioAerobico(1).getNomeComTipoEquipamento());
    }
}
