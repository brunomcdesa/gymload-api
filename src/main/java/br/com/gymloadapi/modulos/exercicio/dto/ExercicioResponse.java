package br.com.gymloadapi.modulos.exercicio.dto;

import br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento;
import br.com.gymloadapi.modulos.comum.enums.ETipoPegada;

public record ExercicioResponse(
    Integer id,
    String nome,
    String descricao,
    String grupoMuscularNome,
    ETipoEquipamento tipoExercicio,
    ETipoPegada tipoPegada
) {
}
