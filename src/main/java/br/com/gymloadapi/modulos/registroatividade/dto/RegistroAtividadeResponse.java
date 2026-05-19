package br.com.gymloadapi.modulos.registroatividade.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record RegistroAtividadeResponse(
    Integer exercicioId,
    String destaque,
    String ultimoPeso,
    String ultimaDistancia,
    Integer ultimaQtdMaxRepeticoes,
    String nomeVariacaoDestaque,
    String nomeVariacaoUltima,
    Integer qtdVariacoes
) {

    public RegistroAtividadeResponse(Integer exercicioId, String destaque, String ultimoPeso,
                                     String ultimaDistancia, Integer ultimaQtdMaxRepeticoes) {
        this(exercicioId, destaque, ultimoPeso, ultimaDistancia, ultimaQtdMaxRepeticoes, null, null, null);
    }

    public RegistroAtividadeResponse comQtdVariacoes(Integer qtd) {
        return new RegistroAtividadeResponse(exercicioId, destaque, ultimoPeso, ultimaDistancia,
            ultimaQtdMaxRepeticoes, nomeVariacaoDestaque, nomeVariacaoUltima, qtd);
    }
}
