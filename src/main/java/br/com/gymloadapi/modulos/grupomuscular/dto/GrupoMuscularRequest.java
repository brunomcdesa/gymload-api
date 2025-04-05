package br.com.gymloadapi.modulos.grupomuscular.dto;

import jakarta.validation.constraints.NotBlank;

public record GrupoMuscularRequest(
    @NotBlank String nome,
    @NotBlank String codigo
) {
}
