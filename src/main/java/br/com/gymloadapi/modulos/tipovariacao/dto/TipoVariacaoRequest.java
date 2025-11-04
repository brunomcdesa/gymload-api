package br.com.gymloadapi.modulos.tipovariacao.dto;

import jakarta.validation.constraints.NotBlank;

public record TipoVariacaoRequest(
    @NotBlank
    String nome
) {
}
