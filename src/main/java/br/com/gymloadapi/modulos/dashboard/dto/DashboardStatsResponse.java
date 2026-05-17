package br.com.gymloadapi.modulos.dashboard.dto;

import java.util.List;

public record DashboardStatsResponse(
    int streak,
    long treinosMes,
    boolean[] diasSemana,
    int prsEssaSemana,
    List<RecordeRecenteResponse> recordesRecentes
) {
}
