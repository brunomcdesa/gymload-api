package br.com.gymloadapi.modulos.exercicio.dto;

import br.com.gymloadapi.modulos.exercicio.predicate.ExercicioPredicate;

public record ExercicioFiltros(
        Integer grupoMuscularId
) {

    public ExercicioPredicate toPredicate() {
        return new ExercicioPredicate()
                .comGrupoMuscularId(grupoMuscularId);
    }
}
