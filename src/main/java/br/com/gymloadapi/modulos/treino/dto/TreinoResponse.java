package br.com.gymloadapi.modulos.treino.dto;

import br.com.gymloadapi.modulos.comum.anotations.DatePatternResponse;
import br.com.gymloadapi.modulos.comum.enums.ESituacao;

import java.time.LocalDate;

public record TreinoResponse(
    Integer id,
    String nome,
    @DatePatternResponse
    LocalDate dataCadastro,
    ESituacao situacao
) {
}
