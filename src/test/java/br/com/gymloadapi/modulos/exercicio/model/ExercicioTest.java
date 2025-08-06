package br.com.gymloadapi.modulos.exercicio.model;

import br.com.gymloadapi.modulos.comum.enums.ETipoExercicio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioMusculacao;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

class ExercicioTest {

    @Test
    void isExercicioMusculacao_deveRetornarTrue_quandoExercicioForMusculacao() {
        assertTrue(umExercicioMusculacao(1).isExercicioMusculacao());
    }

    @ParameterizedTest
    @EnumSource(value = ETipoExercicio.class, names = "MUSCULACAO", mode = EXCLUDE)
    void isExercicioMusculacao_deveRetornarFalse_quandoExercicioNaoForMusculacao(ETipoExercicio tipoExercicio) {
        var exercicio = Exercicio.builder()
            .tipoExercicio(tipoExercicio)
            .build();
        assertFalse(exercicio.isExercicioMusculacao());
    }
}
