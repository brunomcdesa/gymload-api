package br.com.gymloadapi.modulos.dashboard.dto;

public record DashboardStatsResponse(int streak, long treinosMes, boolean[] diasSemana) {
}
