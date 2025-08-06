package br.com.gymloadapi.modulos.exercicio.dto;

import br.com.gymloadapi.modulos.comum.enums.ETipoExercicio;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Aerobico;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Calistenia;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Musculacao;
import br.com.gymloadapi.modulos.comum.utils.ValidacaoUtils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public record ExercicioRequest(
    @NotBlank
    String nome,

    @NotNull
    ETipoExercicio tipoExercicio,

    String descricao,

    @Null(groups = Aerobico.class)
    @NotNull(groups = {Musculacao.class, Calistenia.class})
    Integer grupoMuscularId,

    boolean possuiVariacao
) {

    public void aplicarGroupValidators() {
        ValidacaoUtils.aplicarValidacoes(this, this.tipoExercicio.getGroupValidator());
    }
}
