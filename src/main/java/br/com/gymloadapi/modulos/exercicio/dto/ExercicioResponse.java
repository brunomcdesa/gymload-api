package br.com.gymloadapi.modulos.exercicio.dto;

import br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento;
import br.com.gymloadapi.modulos.comum.enums.ETipoExercicio;
import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record ExercicioResponse(
    Integer id,
    String nome,
    String descricao,
    String grupoMuscularNome,
    ETipoExercicio tipoExercicio,
    ETipoEquipamento tipoEquipamento
) {
}
