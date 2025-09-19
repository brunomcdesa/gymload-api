package br.com.gymloadapi.modulos.tipoVariacao.dto;

import jakarta.validation.constraints.NotBlank;

public record TipoVariacaoRequest(
    @NotBlank
    String nome
) {
}
