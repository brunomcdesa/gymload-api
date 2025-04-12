package br.com.gymloadapi.modulos.registroatividade.registrocarga.dto;

import br.com.gymloadapi.modulos.comum.enums.EUnidadePeso;

import jakarta.validation.constraints.NotNull;

public record HistoricoCargasRequest(
    @NotNull
    Double carga,
    @NotNull
    EUnidadePeso unidadePeso,
    @NotNull
    Integer exercicioId,
    @NotNull
    Integer qtdRepeticoes,
    @NotNull
    Integer qtdSeries
) {
}
