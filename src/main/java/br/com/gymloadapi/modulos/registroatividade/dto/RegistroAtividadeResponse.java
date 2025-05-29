package br.com.gymloadapi.modulos.registroatividade.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record RegistroAtividadeResponse(
    Integer exercicioId,
    String destaque,
    String ultimoPeso,
    String ultimaDistancia
) {
}
