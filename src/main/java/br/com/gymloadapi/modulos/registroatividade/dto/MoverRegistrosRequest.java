package br.com.gymloadapi.modulos.registroatividade.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record MoverRegistrosRequest(
    @NotNull Integer exercicioId,
    @NotEmpty List<Integer> registroIds,
    @NotNull Integer variacaoDestinoId
) {
}
