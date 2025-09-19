package br.com.gymloadapi.modulos.tipoVariacao.dto;

import java.time.LocalDateTime;

public record TipoVariacaoResponse(
    Integer id,
    String nome,
    LocalDateTime dataCadastro
) {
}
