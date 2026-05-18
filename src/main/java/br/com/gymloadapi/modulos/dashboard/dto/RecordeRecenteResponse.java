package br.com.gymloadapi.modulos.dashboard.dto;

import java.time.LocalDate;

public record RecordeRecenteResponse(
    Integer exercicioId,
    String exercicioNome,
    String tipoExercicio,
    String valorPr,
    LocalDate dataPr,
    Integer variacaoId,
    String variacaoNome,
    Boolean possuiVariacao
) {
}
