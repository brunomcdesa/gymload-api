package br.com.gymloadapi.modulos.treino.repository;

import br.com.gymloadapi.modulos.treino.model.Treino;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.EntityManager;
import java.util.Optional;

import static br.com.gymloadapi.modulos.treino.model.QTreino.treino;
import static br.com.gymloadapi.modulos.usuario.model.QUsuario.usuario;

@RequiredArgsConstructor
public class TreinoRepositoryImpl implements TreinoRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public Optional<Treino> findCompleteById(Integer id) {
        return Optional.ofNullable(new JPAQueryFactory(entityManager)
            .selectFrom(treino)
            .leftJoin(treino.exercicios).fetchJoin()
            .leftJoin(treino.usuario, usuario).fetchJoin()
            .where(treino.id.eq(id))
            .fetchFirst());
    }
}
