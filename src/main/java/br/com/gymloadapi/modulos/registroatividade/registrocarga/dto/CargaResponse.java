package br.com.gymloadapi.modulos.registroatividade.registrocarga.dto;

import java.util.List;

public record CargaResponse(
    Double maiorCarga,
    List<HistoricoCargasResponse> historicoCargas
) {
}
