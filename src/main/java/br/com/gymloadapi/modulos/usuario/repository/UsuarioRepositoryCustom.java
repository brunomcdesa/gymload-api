package br.com.gymloadapi.modulos.usuario.repository;

import br.com.gymloadapi.modulos.comum.types.Email;

public interface UsuarioRepositoryCustom {

    boolean existsByEmail(Email email);
}
