package br.com.gymloadapi.modulos.usuario.dto;

import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Alteracao;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Cadastro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;

public record UsuarioRequest(
    @NotBlank(groups = {Cadastro.class, Alteracao.class})
    String nome,
    @NotBlank(groups = {Cadastro.class, Alteracao.class})
    String username,
    @Null(groups = {Alteracao.class})
    @NotBlank(groups = Cadastro.class)
    String password
) {
}
