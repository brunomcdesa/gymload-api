package br.com.gymloadapi.modulos.historicocargas.dto;

import br.com.gymloadapi.modulos.comum.enums.EUnidadePeso;

public record HistoricoCargasRequest(
    Double carga,
    EUnidadePeso unidadePeso,
    Integer exercicioId,
    Integer qtdRepeticoes,
    Integer qtdSeries
) {
}
