package br.com.gymloadapi.modulos.registroatividade.dto;

import br.com.gymloadapi.modulos.comum.enums.EUnidadePeso;
import br.com.gymloadapi.modulos.registroatividade.registrocardio.enums.ETipoVelocidadeMedia;

import jakarta.validation.constraints.NotNull;

public record RegistroAtividadeRequest(
    @NotNull
    Integer exercicioId,

    @NotNull
    Double peso,
    @NotNull
    EUnidadePeso unidadePeso,
    @NotNull
    Integer qtdRepeticoes,
    @NotNull
    Integer qtdSeries,

    Double distancia,
    Double duracao,
    ETipoVelocidadeMedia tipoVelocidadeMedia
) {
}
