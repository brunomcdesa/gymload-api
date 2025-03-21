package br.com.gymloadapi.autenticacao.dto;

import br.com.gymloadapi.modulos.usuario.enums.EUserRole;

import java.util.List;

public record CadastroRequest(
    String nome,
    String username,
    String password,
    List<EUserRole> roles
) {
}
