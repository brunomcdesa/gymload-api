package br.com.gymloadapi.modulos.exercicio.dto;

import br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento;

import jakarta.validation.constraints.NotNull;

public record ExercicioVariacaoRequest(
    @NotNull
    Integer exercicioBaseId,
    @NotNull
    ETipoEquipamento tipoEquipamento
) {
}
