package br.com.gymloadapi.modulos.exercicio.dto;

import br.com.gymloadapi.modulos.exercicio.enums.ETipoExercicio;
import br.com.gymloadapi.modulos.exercicio.enums.ETipoPegada;

public record ExercicioResponse(
    Integer id,
    String nome,
    String descricao,
    String grupoMuscularNome,
    ETipoExercicio tipoExercicio,
    ETipoPegada tipoPegada
) {
}
