package br.com.gymloadapi.modulos.registroatividade.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record RegistroAtividadeFiltros(
    @NotEmpty List<Integer> exerciciosIds
) {
}
