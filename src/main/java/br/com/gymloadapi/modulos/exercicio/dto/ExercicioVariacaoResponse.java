package br.com.gymloadapi.modulos.exercicio.dto;

import br.com.gymloadapi.modulos.tipovariacao.dto.TipoVariacaoResponse;

public record ExercicioVariacaoResponse(
    Integer id,
    String nome,
    TipoVariacaoResponse tipoVariacao
) {
}
