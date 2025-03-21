package br.com.gymloadapi.modulos.usuario.dto;

import java.util.UUID;

public record UsuarioResponse(
    UUID id,
    String nome,
    String username
) {
}
