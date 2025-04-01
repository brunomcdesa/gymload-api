package br.com.gymloadapi.modulos.exercicio.predicate;

import br.com.gymloadapi.modulos.comum.predicate.PredicateBase;
import br.com.gymloadapi.modulos.comum.enums.ETipoExercicio;
import br.com.gymloadapi.modulos.comum.enums.ETipoPegada;

import static br.com.gymloadapi.modulos.exercicio.model.QExercicio.exercicio;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ExercicioPredicate extends PredicateBase {

    public ExercicioPredicate comGrupoMuscularId(Integer grupoMuscularId) {
        if (grupoMuscularId != null) {
            builder.and(exercicio.grupoMuscular.id.eq(grupoMuscularId));
        }
        return this;
    }

    public ExercicioPredicate comNome(String nome) {
        if (isNotBlank(nome)) {
            builder.and(exercicio.nome.containsIgnoreCase(nome));
        }
        return this;
    }

    public ExercicioPredicate comTipoExercicio(ETipoExercicio tipoExercicio) {
        if (tipoExercicio != null) {
            builder.and(exercicio.tipoExercicio.eq(tipoExercicio));
        }
        return this;
    }

    public ExercicioPredicate comTipoPegada(ETipoPegada tipoPegada) {
        if (tipoPegada != null) {
            builder.and(exercicio.tipoPegada.eq(tipoPegada));
        }
        return this;
    }
}
