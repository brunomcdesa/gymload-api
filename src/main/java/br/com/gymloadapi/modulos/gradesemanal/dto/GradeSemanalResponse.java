package br.com.gymloadapi.modulos.gradesemanal.dto;

import br.com.gymloadapi.modulos.comum.enums.EDiaSemana;

public record GradeSemanalResponse(
    EDiaSemana diaSemana,
    Integer treinoId,
    String treinoNome
) {
}
