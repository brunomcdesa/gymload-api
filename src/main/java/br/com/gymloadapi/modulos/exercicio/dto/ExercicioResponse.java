package br.com.gymloadapi.modulos.exercicio.dto;

public record ExercicioResponse(
        String nome,
        String descricao,
        String grupoMuscularNome
) {
}
