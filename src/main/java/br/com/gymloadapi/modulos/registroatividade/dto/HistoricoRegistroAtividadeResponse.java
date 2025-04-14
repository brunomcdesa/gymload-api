package br.com.gymloadapi.modulos.registroatividade.dto;

import br.com.gymloadapi.modulos.comum.anotations.DatePatternResponse;
import br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento;

import java.time.LocalDate;

public record HistoricoRegistroAtividadeResponse(
    Integer id,
    String exercicioNome,
    @DatePatternResponse
    LocalDate dataCadastro,

    String carga,
    ETipoEquipamento tipoExercicio,
    String grupoMuscularNome,
    Integer qtdRepeticoes,
    Integer qtdSeries,

    Double distancia,
    Double duracao
) {
}
