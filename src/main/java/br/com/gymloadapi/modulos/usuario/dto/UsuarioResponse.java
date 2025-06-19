package br.com.gymloadapi.modulos.usuario.dto;

import br.com.gymloadapi.modulos.comum.enums.ESexo;

import java.util.UUID;

public record UsuarioResponse(
    UUID uuid,
    String nome,
    String email,
    String username,
    Integer idade,
    Double pesoCorporal,
    Double altura,
    ESexo sexo
) {
}
