package br.com.gymloadapi.modulos.historicocargas.dto;

import br.com.gymloadapi.modulos.exercicio.enums.ETipoExercicio;

public record HistoricoCargasResponse(
        String exercicioNome,
        String carga,
        ETipoExercicio tipoExercicio,
        String grupoMuscularNome,
        Integer qtdRepeticoes
) {
}
