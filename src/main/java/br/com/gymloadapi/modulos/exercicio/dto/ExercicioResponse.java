package br.com.gymloadapi.modulos.exercicio.dto;

import br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento;
import br.com.gymloadapi.modulos.comum.enums.ETipoExercicio;
import br.com.gymloadapi.modulos.comum.enums.ETipoPegada;
import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record ExercicioResponse(
    Integer id,
    String nome,
    String descricao,
    String grupoMuscularNome,
    //TODO: remover este atributo apos sub ir a proxima versao
    ETipoEquipamento tipoExercicio,
    ETipoExercicio tipoExercicioo,
    ETipoEquipamento tipoEquipamento,
    ETipoPegada tipoPegada
) {
}
