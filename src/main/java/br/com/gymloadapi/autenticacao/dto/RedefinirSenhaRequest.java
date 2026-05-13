package br.com.gymloadapi.autenticacao.dto;

import jakarta.validation.constraints.NotBlank;

public record RedefinirSenhaRequest(
    @NotBlank String identifier,
    @NotBlank String password
) {
}
