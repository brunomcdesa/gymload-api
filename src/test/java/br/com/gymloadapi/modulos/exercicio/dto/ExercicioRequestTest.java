package br.com.gymloadapi.modulos.exercicio.dto;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolationException;

import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class ExercicioRequestTest {

    @Test
    void aplicarGroupValidators_deveLancarException_quandoTipoExercicioAerobicoECamposInvalidos() {
        var request = new ExercicioRequest("teste", AEROBICO, null, 1, false);

        var exception = assertThrowsExactly(
            ConstraintViolationException.class,
            request::aplicarGroupValidators
        );
        assertThat(exception.getMessage())
            .contains("grupoMuscularId: deve ser nulo");
    }

    @Test
    void aplicarGroupValidators_naoDeveLancarException_quandoTipoExercicioAerobicoECamposValidos() {
        var request = new ExercicioRequest("teste", AEROBICO, null, null, false);
        assertDoesNotThrow(request::aplicarGroupValidators);
    }

    @Test
    void aplicarGroupValidators_deveLancarException_quandoTipoExercicioCalisteniaECamposInvalidos() {
        var request = new ExercicioRequest("teste", CALISTENIA, null, null, false);

        var exception = assertThrowsExactly(
            ConstraintViolationException.class,
            request::aplicarGroupValidators
        );
        assertThat(exception.getMessage())
            .contains("grupoMuscularId: é obrigatório.");
    }

    @Test
    void aplicarGroupValidators_naoDeveLancarException_quandoTipoExercicioCalisteniaECamposValidos() {
        var request = new ExercicioRequest("teste", CALISTENIA, null, 1, false);
        assertDoesNotThrow(request::aplicarGroupValidators);
    }

    @Test
    void aplicarGroupValidators_deveLancarException_quandoTipoExercicioMusculacaoECamposInvalidos() {
        var request = new ExercicioRequest("teste", MUSCULACAO, null, null, false);

        var exception = assertThrowsExactly(
            ConstraintViolationException.class,
            request::aplicarGroupValidators
        );
        assertThat(exception.getMessage())
            .contains("grupoMuscularId: é obrigatório.");
    }

    @Test
    void aplicarGroupValidators_naoDeveLancarException_quandoTipoExercicioMusculacaoECamposValidos() {
        var request = new ExercicioRequest("teste", MUSCULACAO, null, 1, false);
        assertDoesNotThrow(request::aplicarGroupValidators);
    }
}
