package br.com.gymloadapi.modulos.historicocargas.dto;

import br.com.gymloadapi.modulos.historicocargas.enums.EUnidadePeso;

import java.util.UUID;

public record HistoricoCargasRequest(
    Double carga,
    EUnidadePeso unidadePeso,
    Integer exercicioId,
    Integer qtdRepeticoes,
    UUID usuarioId
) {
}
