package br.com.gymloadapi.modulos.registroatividade.dto;

import br.com.gymloadapi.modulos.comum.anotations.DatePatternResponse;
import br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record HistoricoRegistroAtividadeResponse(
    Integer id,
    String exercicioNome,
    String observacao,
    @DatePatternResponse
    LocalDate dataCadastro,
    String carga,
    ETipoEquipamento tipoExercicio,
    String grupoMuscularNome,
    Integer qtdRepeticoes,
    Integer qtdSeries,
    Double distancia,
    Double duracao,
    String velocidadeMedia
) {
}
