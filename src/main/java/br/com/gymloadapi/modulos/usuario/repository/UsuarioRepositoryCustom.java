package br.com.gymloadapi.modulos.usuario.repository;

import br.com.gymloadapi.modulos.comum.types.Email;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UsuarioRepositoryCustom {

    boolean existsByEmail(Email email);

    Optional<UserDetails> findByEmail(Email email);
}
