package br.com.gymloadapi.modulos.exercicio.dto;

import com.querydsl.core.BooleanBuilder;
import org.junit.jupiter.api.Test;

import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioFiltro;
import static br.com.gymloadapi.modulos.exercicio.model.QExercicio.exercicio;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExercicioFiltroTest {

    @Test
    void toPredicate_deveMontarPredicate_quandoSolicitado() {
        var filtro = umExercicioFiltro();

        assertEquals(new BooleanBuilder().and(exercicio.grupoMuscular.id.eq(2)), filtro.toPredicate());
    }
}
