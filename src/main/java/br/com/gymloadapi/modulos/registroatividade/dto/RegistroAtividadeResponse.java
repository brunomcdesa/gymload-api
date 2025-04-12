package br.com.gymloadapi.modulos.registroatividade.dto;

import java.util.List;

public record RegistroAtividadeResponse(
    String destaque,
    List<HistoricoRegistroAtividadeResponse> historicoRegistroAtividade
) {
}
