package br.com.gymloadapi.modulos.usuario.dto;

import java.util.UUID;

public record UsuarioResponse(
    UUID uuid,
    String nome,
    String username
) {
}
