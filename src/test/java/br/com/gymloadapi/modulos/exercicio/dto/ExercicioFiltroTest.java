package br.com.gymloadapi.modulos.exercicio.dto;

import br.com.gymloadapi.modulos.comum.enums.ETipoExercicio;
import com.querydsl.core.BooleanBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import jakarta.validation.ConstraintViolationException;

import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.AEROBICO;
import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.CALISTENIA;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioFiltro;
import static br.com.gymloadapi.modulos.exercicio.model.QExercicio.exercicio;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

class ExercicioFiltroTest {

    @Test
    void aplicarGroupValidators_naoDeveAplicarGroupValidator_quandoTipoExercicioNaoPossuirGrupoMuscular() {
        var filtros = new ExercicioFiltro(AEROBICO, null);

        assertDoesNotThrow(filtros::aplicarGroupValidators);
    }

    @ParameterizedTest
    @SuppressWarnings("LineLength")
    @EnumSource(value = ETipoExercicio.class, names = {"AEROBICO"}, mode = EXCLUDE)
    void aplicarGroupValidators_deveAplicarGroupValidatorELancarException_quandoTipoExercicioPrecisarDeGrupoMuscularENaoReceberNoFiltro(ETipoExercicio tipoExercicio) {
        var filtros = new ExercicioFiltro(tipoExercicio, null);

        var exception = assertThrowsExactly(ConstraintViolationException.class, filtros::aplicarGroupValidators);
        assertThat(exception.getMessage())
            .contains("grupoMuscularId: é obrigatório.");
    }

    @ParameterizedTest
    @SuppressWarnings("LineLength")
    @EnumSource(value = ETipoExercicio.class, names = {"AEROBICO"}, mode = EXCLUDE)
    void aplicarGroupValidators_deveAplicarGroupValidatorENaoLancarException_quandoTipoExercicioPrecisarDeGrupoMuscularEReceberNoFiltro(ETipoExercicio tipoExercicio) {
        var filtros = new ExercicioFiltro(tipoExercicio, 1);

        assertDoesNotThrow(filtros::aplicarGroupValidators);
    }

    @Test
    void toPredicate_deveMontarPredicate_quandoSolicitado() {
        var filtro = umExercicioFiltro();

        assertEquals(new BooleanBuilder()
                .and(exercicio.tipoExercicio.eq(CALISTENIA))
                .and(exercicio.grupoMuscular.id.eq(2)),
            filtro.toPredicate());
    }
}
