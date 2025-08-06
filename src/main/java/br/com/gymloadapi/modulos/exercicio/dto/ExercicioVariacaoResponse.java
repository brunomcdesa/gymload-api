package br.com.gymloadapi.modulos.exercicio.dto;

import br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento;

public record ExercicioVariacaoResponse(
    Integer id,
    String nome,
    ETipoEquipamento tipoEquipamento
) {
}
