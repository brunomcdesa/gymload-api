package br.com.gymloadapi.modulos.exercicio.dto;

import br.com.gymloadapi.modulos.exercicio.enums.ETipoExercicio;

public record ExercicioResponse(
        String nome,
        String descricao,
        String grupoMuscularNome,
        ETipoExercicio tipoExercicio
) {
}
