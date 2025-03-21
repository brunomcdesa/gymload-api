package br.com.gymloadapi.autenticacao.dto;

public record LoginRequest(
    String username,
    String password
) {
}
