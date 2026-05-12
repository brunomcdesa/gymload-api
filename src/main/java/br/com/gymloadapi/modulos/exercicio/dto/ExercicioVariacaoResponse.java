package br.com.gymloadapi.modulos.exercicio.dto;

import br.com.gymloadapi.modulos.tipovariacao.dto.TipoVariacaoResponse;
import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record ExercicioVariacaoResponse(
    Integer id,
    String nome,
    TipoVariacaoResponse tipoVariacao,
    String ultimaCarga,
    String ultimaDistancia,
    String ultimaSerie
) {
}
