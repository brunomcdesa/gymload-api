package br.com.gymloadapi.autenticacao.dto;

import jakarta.validation.constraints.NotBlank;

public record CadastroRequest(
    @NotBlank String nome,
    @NotBlank String username,
    @NotBlank String password,
    boolean cadastroAdmin
) {
}
