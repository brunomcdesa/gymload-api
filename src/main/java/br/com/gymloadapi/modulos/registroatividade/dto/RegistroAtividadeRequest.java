package br.com.gymloadapi.modulos.registroatividade.dto;

import br.com.gymloadapi.modulos.comum.enums.EUnidadePeso;
import br.com.gymloadapi.modulos.registroatividade.registrocardio.enums.ETipoVelocidadeMedia;

public record RegistroAtividadeRequest(
    Integer exercicioId,

    Double peso,
    EUnidadePeso unidadePeso,
    Integer qtdRepeticoes,
    Integer qtdSeries,

    Double distancia,
    Double duracao,
    ETipoVelocidadeMedia tipoVelocidadeMedia
) {
}
