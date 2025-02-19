package br.com.gymloadapi.modulos.exercicio.dto;

import br.com.gymloadapi.modulos.exercicio.enums.ETipoExercicio;
import br.com.gymloadapi.modulos.exercicio.enums.ETipoPegada;

public record ExercicioRequest(
    String nome,
    String descricao,
    Integer grupoMuscularId,
    ETipoExercicio tipoExercicio,
    ETipoPegada tipoPegada
) {
}
