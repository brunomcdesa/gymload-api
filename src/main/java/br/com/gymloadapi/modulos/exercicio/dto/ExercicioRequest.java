package br.com.gymloadapi.modulos.exercicio.dto;

import br.com.gymloadapi.modulos.comum.enums.ETipoExercicio;
import br.com.gymloadapi.modulos.comum.enums.ETipoPegada;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExercicioRequest(
    @NotBlank String nome,
    String descricao,
    @NotNull Integer grupoMuscularId,
    @NotNull ETipoExercicio tipoExercicio,
    @NotNull ETipoPegada tipoPegada
) {
}
