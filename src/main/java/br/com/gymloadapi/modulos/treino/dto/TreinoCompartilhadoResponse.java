package br.com.gymloadapi.modulos.treino.dto;

import br.com.gymloadapi.modulos.comum.anotations.DatePatternResponse;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioResponse;

import java.time.LocalDate;
import java.util.List;

public record TreinoCompartilhadoResponse(
    String nomeTreino,
    List<ExercicioResponse> exercicios,
    @DatePatternResponse
    LocalDate dataExpiracao
) {
}
