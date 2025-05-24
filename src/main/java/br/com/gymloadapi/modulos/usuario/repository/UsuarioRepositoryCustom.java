package br.com.gymloadapi.modulos.usuario.repository;

import br.com.gymloadapi.modulos.comum.types.Email;

public interface UsuarioRepositoryCustom {

    void atualizarSenha(String username, String senha);

    boolean existsByEmail(Email email);
}
