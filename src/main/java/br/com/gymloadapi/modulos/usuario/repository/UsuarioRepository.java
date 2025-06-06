package br.com.gymloadapi.modulos.usuario.repository;

import br.com.gymloadapi.modulos.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>, QuerydslPredicateExecutor<Usuario>,
    UsuarioRepositoryCustom {

    Optional<UserDetails> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<Usuario> findByUuid(UUID uuid);
}
