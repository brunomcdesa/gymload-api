package br.com.gymloadapi.modulos.historicocargas.dto;

import br.com.gymloadapi.modulos.comum.anotations.DatePatternResponse;
import br.com.gymloadapi.modulos.exercicio.enums.ETipoExercicio;

import java.time.LocalDate;

public record HistoricoCargasResponse(
    Integer id,
    String exercicioNome,
    String carga,
    ETipoExercicio tipoExercicio,
    String grupoMuscularNome,
    Integer qtdRepeticoes,
    Integer qtdSeries,
    @DatePatternResponse
    LocalDate dataCadastro
) {
}
