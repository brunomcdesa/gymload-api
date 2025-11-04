package br.com.gymloadapi.modulos.tipovariacao.dto;

import java.time.LocalDateTime;

public record TipoVariacaoResponse(
    Integer id,
    String nome,
    LocalDateTime dataCadastro
) {
}
