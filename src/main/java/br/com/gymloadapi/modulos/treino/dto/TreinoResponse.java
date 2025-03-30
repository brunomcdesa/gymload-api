package br.com.gymloadapi.modulos.treino.dto;

import br.com.gymloadapi.modulos.comum.anotations.DatePatternResponse;

import java.time.LocalDate;

public record TreinoResponse(
    Integer id,
    String nome,
    @DatePatternResponse
    LocalDate dataCadastro
) {
}
