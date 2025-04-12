package br.com.gymloadapi.modulos.registroatividade.dto;

import br.com.gymloadapi.modulos.comum.anotations.DatePatternResponse;
import br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento;

import java.time.LocalDate;

public record HistoricoRegistroAtividadeResponse(
    Integer id,
    String exercicioNome,
    String carga,
    ETipoEquipamento tipoExercicio,
    String grupoMuscularNome,
    Integer qtdRepeticoes,
    Integer qtdSeries,
    @DatePatternResponse
    LocalDate dataCadastro
) {
}
