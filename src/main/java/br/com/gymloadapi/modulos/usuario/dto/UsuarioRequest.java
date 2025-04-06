package br.com.gymloadapi.modulos.usuario.dto;

import jakarta.validation.constraints.NotBlank;

public record UsuarioRequest(
    @NotBlank String nome,
    @NotBlank String username,
    @NotBlank String password
) {
}
