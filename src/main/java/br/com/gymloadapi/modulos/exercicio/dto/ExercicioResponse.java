package br.com.gymloadapi.modulos.exercicio.dto;

import br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento;
import br.com.gymloadapi.modulos.comum.enums.ETipoExercicio;

public record ExercicioResponse(
    Integer id,
    String nome,
    String descricao,
    Integer grupoMuscularId,
    String grupoMuscularNome,
    ETipoExercicio tipoExercicio,
    ETipoEquipamento tipoEquipamento
) {
}
