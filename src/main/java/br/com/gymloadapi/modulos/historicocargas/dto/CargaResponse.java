package br.com.gymloadapi.modulos.historicocargas.dto;

import java.util.List;

public record CargaResponse(
    Double maiorCarga,
    List<HistoricoCargasResponse> historicoCargas
) {
}
