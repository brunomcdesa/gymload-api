package br.com.gymloadapi.modulos.treino.dto;

import jakarta.validation.constraints.NotBlank;

public record TreinoImportarRequest(
    @NotBlank
    String codigo,
    @NotBlank
    String nome
) {
}
