package br.com.gymloadapi.modulos.exercicio.predicate;

import com.querydsl.core.BooleanBuilder;
import org.junit.jupiter.api.Test;

import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.MUSCULACAO;
import static br.com.gymloadapi.modulos.exercicio.model.QExercicio.exercicio;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExercicioPredicateTest {

    @Test
    void comTipoExercicio_deveMontarPredicate_quandoTipoExercicioValido() {
        var predicate = new ExercicioPredicate().comTipoExercicio(MUSCULACAO).build();

        assertEquals(new BooleanBuilder().and(exercicio.tipoExercicio.eq(MUSCULACAO)), predicate);
    }

    @Test
    void comTipoExercicio_naoDeveMontarPredicate_quandoTipoExercicioInvalido() {
        var predicate = new ExercicioPredicate().comTipoExercicio(null).build();

        assertEquals(new BooleanBuilder(), predicate);
    }

    @Test
    void comGrupoMuscularId_deveMontarPredicate_quandoGrupoMuscularIdValido() {
        var predicate = new ExercicioPredicate().comGrupoMuscularId(1).build();

        assertEquals(new BooleanBuilder().and(exercicio.grupoMuscular.id.eq(1)), predicate);
    }

    @Test
    void comGrupoMuscularId_naoDeveMontarPredicate_quandoGrupoMuscularIdInvalido() {
        var predicate = new ExercicioPredicate().comGrupoMuscularId(null).build();

        assertEquals(new BooleanBuilder(), predicate);
    }
}
