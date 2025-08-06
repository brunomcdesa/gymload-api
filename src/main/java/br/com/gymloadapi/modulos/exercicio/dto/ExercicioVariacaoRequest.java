package br.com.gymloadapi.modulos.exercicio.dto;

import br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento;
import br.com.gymloadapi.modulos.comum.enums.ETipoExercicio;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Aerobico;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Calistenia;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Musculacao;
import br.com.gymloadapi.modulos.comum.utils.ValidacaoUtils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public record ExercicioVariacaoRequest(
    @NotNull
    Integer exercicioBaseId,

    @Null(groups = {Musculacao.class, Aerobico.class})
    @NotBlank(groups = Calistenia.class)
    String nome,

    @Null(groups = {Aerobico.class, Calistenia.class})
    @NotNull(groups = Musculacao.class)
    ETipoEquipamento tipoEquipamento
) {

    public void aplicarGroupValidators(ETipoExercicio tipoExercicio) {
        ValidacaoUtils.aplicarValidacoes(this, tipoExercicio.getGroupValidator());
    }
}
