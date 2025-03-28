package br.com.gymloadapi.modulos.treino.dto;

import br.com.gymloadapi.modulos.comum.anotations.DatePatternResponse;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioResponse;

import java.time.LocalDate;
import java.util.List;

public record TreinoResponse(
    Integer id,
    String nome,
    @DatePatternResponse
    LocalDate dataCadastro,
    List<ExercicioResponse> exercicios
) {
}
