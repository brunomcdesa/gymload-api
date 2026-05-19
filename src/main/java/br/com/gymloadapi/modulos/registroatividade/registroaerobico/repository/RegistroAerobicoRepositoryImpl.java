package br.com.gymloadapi.modulos.registroatividade.registroaerobico.repository;

import br.com.gymloadapi.modulos.registroatividade.registroaerobico.model.RegistroAerobico;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static br.com.gymloadapi.modulos.exercicio.model.QExercicio.exercicio;
import static br.com.gymloadapi.modulos.exercicio.model.QExercicioVariacao.exercicioVariacao;
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
                .and(registroAerobico.exercicioVariacao.isNull())
                .and(registroAerobico.usuario.id.eq(usuarioId)))
            .fetch();
    }

    @Override
    public List<RegistroAerobico> findAllByExercicioIdAndUsuarioIdIncluindoVariacoes(Integer exercicioId,
                                                                                      Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(registroAerobico)
            .innerJoin(registroAerobico.exercicio, exercicio).fetchJoin()
            .innerJoin(registroAerobico.usuario, usuario).fetchJoin()
            .leftJoin(registroAerobico.exercicioVariacao, exercicioVariacao).fetchJoin()
            .where(registroAerobico.exercicio.id.eq(exercicioId)
                .and(registroAerobico.usuario.id.eq(usuarioId)))
            .fetch();
    }

    @Override
    public List<RegistroAerobico> findAllByExercicioIdAndVariacaoIdAndUsuarioId(Integer exercicioId,
                                                                                 Integer variacaoId,
                                                                                 Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(registroAerobico)
            .innerJoin(registroAerobico.exercicio, exercicio).fetchJoin()
            .innerJoin(registroAerobico.usuario, usuario).fetchJoin()
            .where(registroAerobico.exercicio.id.eq(exercicioId)
                .and(registroAerobico.exercicioVariacao.id.eq(variacaoId))
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

    @Override
    public boolean existeRegistroHojeParaExercicios(List<Integer> exercicioIds, Integer usuarioId, LocalDate hoje) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(registroAerobico)
            .where(registroAerobico.exercicio.id.in(exercicioIds)
                .and(registroAerobico.usuario.id.eq(usuarioId))
                .and(registroAerobico.dataCadastro.eq(hoje)))
            .fetchFirst() != null;
    }

    @Override
    public void migrarRegistrosSemVariacao(Integer exercicioId, Integer variacaoPadraoId) {
        new JPAQueryFactory(entityManager)
            .update(registroAerobico)
            .where(registroAerobico.exercicio.id.eq(exercicioId)
                .and(registroAerobico.exercicioVariacao.isNull()))
            .set(registroAerobico.exercicioVariacao.id, variacaoPadraoId)
            .execute();
    }

    @Override
    public void moverRegistros(List<Integer> registroIds, Integer variacaoDestinoId, Integer usuarioId) {
        new JPAQueryFactory(entityManager)
            .update(registroAerobico)
            .where(registroAerobico.id.in(registroIds)
                .and(registroAerobico.usuario.id.eq(usuarioId)))
            .set(registroAerobico.exercicioVariacao.id, variacaoDestinoId)
            .execute();
    }

    @Override
    public List<RegistroAerobico> findTodosPrsPorUsuarioId(Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(registroAerobico)
            .innerJoin(registroAerobico.exercicio, exercicio).fetchJoin()
            .innerJoin(registroAerobico.usuario, usuario).fetchJoin()
            .leftJoin(registroAerobico.exercicioVariacao, exercicioVariacao).fetchJoin()
            .where(registroAerobico.usuario.id.eq(usuarioId))
            .fetch();
    }
}
