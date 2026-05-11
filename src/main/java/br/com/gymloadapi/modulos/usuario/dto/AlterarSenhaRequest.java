package br.com.gymloadapi.modulos.usuario.dto;

import jakarta.validation.constraints.NotBlank;

public record AlterarSenhaRequest(
    @NotBlank
    String senhaAtual,

    @NotBlank
    String novaSenha
) {
}
