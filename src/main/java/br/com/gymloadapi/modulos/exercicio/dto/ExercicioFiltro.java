package br.com.gymloadapi.modulos.exercicio.dto;

import br.com.gymloadapi.modulos.comum.enums.ETipoExercicio;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Calistenia;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Musculacao;
import br.com.gymloadapi.modulos.comum.utils.ValidacaoUtils;
import br.com.gymloadapi.modulos.exercicio.predicate.ExercicioPredicate;
import com.querydsl.core.types.Predicate;

import jakarta.validation.constraints.NotNull;

import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.deveConterGrupoMuscular;

public record ExercicioFiltro(
    @NotNull
    ETipoExercicio tipoExercicio,
    @NotNull(groups = {Calistenia.class, Musculacao.class})
    Integer grupoMuscularId
) {

    public void aplicarGroupValidators() {
        if (deveConterGrupoMuscular(this.tipoExercicio)) {
            ValidacaoUtils.aplicarValidacoes(this, this.tipoExercicio.getGroupValidator());
        }
    }

    public Predicate toPredicate() {
        return new ExercicioPredicate()
            .comTipoExercicio(tipoExercicio)
            .comGrupoMuscularId(grupoMuscularId)
            .build();
    }
}
