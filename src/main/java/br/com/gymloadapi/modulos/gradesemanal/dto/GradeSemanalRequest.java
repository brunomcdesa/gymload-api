package br.com.gymloadapi.modulos.gradesemanal.dto;

import jakarta.validation.constraints.NotNull;

public record GradeSemanalRequest(
    @NotNull
    Integer treinoId
) {
}
