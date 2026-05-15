package br.com.gymloadapi.modulos.treino.dto;

import br.com.gymloadapi.modulos.comum.anotations.DatePatternResponse;

import java.time.LocalDate;

public record TreinoCompartilhamentoTokenResponse(
    String codigo,
    @DatePatternResponse
    LocalDate dataExpiracao
) {
}
