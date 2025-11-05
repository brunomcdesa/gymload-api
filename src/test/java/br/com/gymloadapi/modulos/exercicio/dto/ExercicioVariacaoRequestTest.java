package br.com.gymloadapi.modulos.exercicio.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.ConstraintViolationException;

import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class ExercicioVariacaoRequestTest {

    @Test
    void aplicarGroupValidators_deveLancarException_quandoTipoExercicioAerobicoECamposInvalidos() {
        var request = new ExercicioVariacaoRequest(1, "teste", 2);

        var exception = assertThrowsExactly(
            ConstraintViolationException.class,
            () -> request.aplicarGroupValidators(AEROBICO)
        );
        assertThat(exception.getMessage())
            .contains("tipoVariacaoId: deve ser nulo",
                "nome: deve ser nulo");
    }

    @Test
    void aplicarGroupValidators_naoDeveLancarException_quandoTipoExercicioAerobicoECamposValidos() {
        var request = new ExercicioVariacaoRequest(null, null, null);
        assertDoesNotThrow(() -> request.aplicarGroupValidators(AEROBICO));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void aplicarGroupValidators_deveLancarException_quandoTipoExercicioCalisteniaECamposInvalidos(String nomeInvalido) {
        var request = new ExercicioVariacaoRequest(null, nomeInvalido, 1);

        var exception = assertThrowsExactly(
            ConstraintViolationException.class,
            () -> request.aplicarGroupValidators(CALISTENIA)
        );
        assertThat(exception.getMessage())
            .contains("tipoVariacaoId: deve ser nulo",
                "nome: é obrigatório.");
    }

    @Test
    void aplicarGroupValidators_naoDeveLancarException_quandoTipoExercicioCalisteniaECamposValidos() {
        var request = new ExercicioVariacaoRequest(1, "teste", null);
        assertDoesNotThrow(() -> request.aplicarGroupValidators(CALISTENIA));
    }

    @Test
    void aplicarGroupValidators_deveLancarException_quandoTipoExercicioMusculacaoECamposInvalidos() {
        var request = new ExercicioVariacaoRequest(null, "teste", null);

        var exception = assertThrowsExactly(
            ConstraintViolationException.class,
            () -> request.aplicarGroupValidators(MUSCULACAO)
        );
        assertThat(exception.getMessage())
            .contains("tipoVariacaoId: é obrigatório.",
                "nome: deve ser nulo");
    }

    @Test
    void aplicarGroupValidators_naoDeveLancarException_quandoTipoExercicioMusculacaoECamposValidos() {
        var request = new ExercicioVariacaoRequest(1, null, 2);
        assertDoesNotThrow(() -> request.aplicarGroupValidators(MUSCULACAO));
    }
}
