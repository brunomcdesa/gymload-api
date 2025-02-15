package br.com.gymloadapi.modulos.exercicio.dto;

public record ExercicioRequest(
        String nome,
        String descricao,
        Integer grupoMuscularId
) {
}
