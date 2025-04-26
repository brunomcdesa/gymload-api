package br.com.gymloadapi.modulos.treino.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record TreinoRequest(
    @NotBlank
    String nome,
    @NotEmpty
    List<Integer> exerciciosIds
) {
}
