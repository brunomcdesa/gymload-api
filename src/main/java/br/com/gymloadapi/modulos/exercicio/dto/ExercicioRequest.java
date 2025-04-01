package br.com.gymloadapi.modulos.exercicio.dto;

import br.com.gymloadapi.modulos.comum.enums.ETipoExercicio;
import br.com.gymloadapi.modulos.comum.enums.ETipoPegada;

public record ExercicioRequest(
    String nome,
    String descricao,
    Integer grupoMuscularId,
    ETipoExercicio tipoExercicio,
    ETipoPegada tipoPegada
) {
}
