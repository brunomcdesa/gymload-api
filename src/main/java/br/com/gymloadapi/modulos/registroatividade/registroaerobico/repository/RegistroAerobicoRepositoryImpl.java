package br.com.gymloadapi.modulos.registroatividade.registroaerobico.repository;

import br.com.gymloadapi.modulos.registroatividade.registroaerobico.model.RegistroAerobico;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static br.com.gymloadapi.modulos.exercicio.model.QExercicio.exercicio;
import static br.com.gymloadapi.modulos.registroatividade.registroaerobico.model.QRegistroAerobico.registroAerobico;
import static br.com.gymloadapi.modulos.usuario.model.QUsuario.usuario;

@RequiredArgsConstructor
public class RegistroAerobicoRepositoryImpl implements RegistroAerobicoRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<RegistroAerobico> findAllByExercicioIdAndUsuarioId(Integer exercicioId, Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(registroAerobico)
            .innerJoin(registroAerobico.exercicio, exercicio).fetchJoin()
            .innerJoin(registroAerobico.usuario, usuario).fetchJoin()
            .where(registroAerobico.exercicio.id.eq(exercicioId)
                .and(registroAerobico.usuario.id.eq(usuarioId)))
            .fetch();
    }

    @Override
    public Optional<RegistroAerobico> findLastByExercicioIdAndUsuarioId(Integer exercicioId, Integer usuarioId) {
        return Optional.ofNullable(new JPAQueryFactory(entityManager)
            .selectFrom(registroAerobico)
            .innerJoin(registroAerobico.exercicio, exercicio).fetchJoin()
            .innerJoin(registroAerobico.usuario, usuario).fetchJoin()
            .where(registroAerobico.exercicio.id.eq(exercicioId)
                .and(registroAerobico.usuario.id.eq(usuarioId)))
            .orderBy(registroAerobico.id.desc())
            .fetchFirst()
        );
    }
}
