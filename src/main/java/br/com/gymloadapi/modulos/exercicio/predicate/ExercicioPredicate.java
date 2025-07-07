package br.com.gymloadapi.modulos.exercicio.predicate;

import br.com.gymloadapi.modulos.comum.enums.ETipoExercicio;
import br.com.gymloadapi.modulos.comum.predicate.PredicateBase;

import static br.com.gymloadapi.modulos.exercicio.model.QExercicio.exercicio;

public class ExercicioPredicate extends PredicateBase {

    public ExercicioPredicate comTipoExercicio(ETipoExercicio tipoExercicio) {
        if (tipoExercicio != null) {
            builder.and(exercicio.tipoExercicio.eq(tipoExercicio));
        }

        return this;
    }

    public ExercicioPredicate comGrupoMuscularId(Integer grupoMuscularId) {
        if (grupoMuscularId != null) {
            builder.and(exercicio.grupoMuscular.id.eq(grupoMuscularId));
        }

        return this;
    }
}
