package br.com.gymloadapi.modulos.historicoCargas.dto;

import br.com.gymloadapi.modulos.historicoCargas.enums.EUnidadePeso;

public record HistoricoCargasRequest(
        Double carga,
        EUnidadePeso unidadePeso,
        Integer exercicioId
) {
}
