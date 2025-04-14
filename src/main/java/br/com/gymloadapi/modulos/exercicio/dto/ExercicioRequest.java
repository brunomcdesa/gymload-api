package br.com.gymloadapi.modulos.exercicio.dto;

import br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento;
import br.com.gymloadapi.modulos.comum.enums.ETipoExercicio;
import br.com.gymloadapi.modulos.comum.enums.ETipoPegada;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Aerobico;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Musculacao;
import br.com.gymloadapi.modulos.comum.utils.ValidacaoUtils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.MUSCULACAO;

public record ExercicioRequest(
    @NotBlank
    String nome,

    @NotNull
    ETipoExercicio tipoExercicio,

    String descricao,

    @Null(groups = Aerobico.class)
    @NotNull(groups = Musculacao.class)
    Integer grupoMuscularId,

    @Null(groups = Aerobico.class)
    @NotNull(groups = Musculacao.class)
    ETipoEquipamento tipoEquipamento,

    @Null(groups = Aerobico.class)
    @NotNull(groups = Musculacao.class)
    ETipoPegada tipoPegada
) {

    public void aplicarGroupValidators() {
        ValidacaoUtils.aplicarValidacoes(this, this.tipoExercicio.getGroupValidator());
    }

    public boolean isExercicioMusculacao() {
        return this.tipoExercicio == MUSCULACAO;
    }
}
