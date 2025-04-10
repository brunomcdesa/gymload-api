package br.com.gymloadapi.modulos.usuario.repository;

import com.querydsl.jpa.impl.JPAUpdateClause;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import static br.com.gymloadapi.modulos.usuario.model.QUsuario.usuario;

@RequiredArgsConstructor
public class UsuarioRepositoryImpl implements UsuarioRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    @Transactional
    public void atualizarSenha(String username, String senha) {
        new JPAUpdateClause(entityManager, usuario)
            .set(usuario.senha, senha)
            .where(usuario.username.eq(username))
            .execute();
    }
}
