package br.com.gymloadapi.modulos.exercicio.dto;

import br.com.gymloadapi.modulos.comum.enums.ETipoExercicio;
import br.com.gymloadapi.modulos.comum.enums.ETipoPegada;
import br.com.gymloadapi.modulos.exercicio.predicate.ExercicioPredicate;

public record ExercicioFiltros(
    Integer grupoMuscularId,
    String nome,
    ETipoExercicio tipoExercicio,
    ETipoPegada tipoPegada
) {

    public ExercicioPredicate toPredicate() {
        return new ExercicioPredicate()
            .comGrupoMuscularId(grupoMuscularId)
            .comNome(nome)
            .comTipoExercicio(tipoExercicio)
            .comTipoPegada(tipoPegada);
    }
}
