package br.com.gymloadapi.modulos.treino.dto;

import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Alteracao;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Cadastro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record TreinoRequest(
    @NotBlank(groups = Cadastro.class)
    String nome,
    @NotEmpty(groups = {Cadastro.class, Alteracao.class})
    List<Integer> exerciciosIds
) {
}
