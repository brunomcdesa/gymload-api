package br.com.gymloadapi.modulos.usuario.repository;

import br.com.gymloadapi.modulos.comum.types.Email;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.EntityManager;

import static br.com.gymloadapi.modulos.usuario.model.QUsuario.usuario;

@RequiredArgsConstructor
public class UsuarioRepositoryImpl implements UsuarioRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public boolean existsByEmail(Email email) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(usuario)
            .where(usuario.email.eq(email))
            .fetchFirst() != null;
    }
}
