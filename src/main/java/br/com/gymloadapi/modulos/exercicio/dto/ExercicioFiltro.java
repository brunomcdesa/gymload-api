package br.com.gymloadapi.modulos.exercicio.dto;

import br.com.gymloadapi.modulos.exercicio.predicate.ExercicioPredicate;
import com.querydsl.core.types.Predicate;

public record ExercicioFiltro(
    Integer grupoMuscularId
) {

    public Predicate toPredicate() {
        return new ExercicioPredicate()
            .comGrupoMuscularId(grupoMuscularId)
            .build();
    }
}
