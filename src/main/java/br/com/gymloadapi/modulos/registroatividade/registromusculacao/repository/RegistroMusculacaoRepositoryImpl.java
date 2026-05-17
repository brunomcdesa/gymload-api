package br.com.gymloadapi.modulos.registroatividade.registromusculacao.repository;

import br.com.gymloadapi.modulos.registroatividade.registromusculacao.model.RegistroMusculacao;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static br.com.gymloadapi.modulos.exercicio.model.QExercicio.exercicio;
import static br.com.gymloadapi.modulos.grupomuscular.model.QGrupoMuscular.grupoMuscular;
import static br.com.gymloadapi.modulos.registroatividade.registromusculacao.model.QRegistroMusculacao.registroMusculacao;
import static br.com.gymloadapi.modulos.usuario.model.QUsuario.usuario;

@RequiredArgsConstructor
public class RegistroMusculacaoRepositoryImpl implements RegistroMusculacaoRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<RegistroMusculacao> findAllByExercicioIdAndUsuarioId(Integer exercicioId, Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(registroMusculacao)
            .innerJoin(registroMusculacao.exercicio, exercicio).fetchJoin()
            .innerJoin(exercicio.grupoMuscular, grupoMuscular).fetchJoin()
            .innerJoin(registroMusculacao.usuario, usuario).fetchJoin()
            .where(registroMusculacao.exercicio.id.eq(exercicioId)
                .and(registroMusculacao.exercicioVariacao.isNull())
                .and(registroMusculacao.usuario.id.eq(usuarioId)))
            .fetch();
    }

    @Override
    public List<RegistroMusculacao> findAllByExercicioIdAndVariacaoIdAndUsuarioId(Integer exercicioId,
                                                                                   Integer variacaoId,
                                                                                   Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(registroMusculacao)
            .innerJoin(registroMusculacao.exercicio, exercicio).fetchJoin()
            .innerJoin(exercicio.grupoMuscular, grupoMuscular).fetchJoin()
            .innerJoin(registroMusculacao.usuario, usuario).fetchJoin()
            .where(registroMusculacao.exercicio.id.eq(exercicioId)
                .and(registroMusculacao.exercicioVariacao.id.eq(variacaoId))
                .and(registroMusculacao.usuario.id.eq(usuarioId)))
            .fetch();
    }

    @Override
    public Optional<RegistroMusculacao> findLastByExercicioIdAndUsuarioId(Integer exercicioId, Integer usuarioId) {
        return Optional.ofNullable(new JPAQueryFactory(entityManager)
            .selectFrom(registroMusculacao)
            .innerJoin(registroMusculacao.exercicio, exercicio).fetchJoin()
            .innerJoin(registroMusculacao.usuario, usuario).fetchJoin()
            .where(registroMusculacao.exercicio.id.eq(exercicioId)
                .and(registroMusculacao.usuario.id.eq(usuarioId)))
            .orderBy(registroMusculacao.id.desc())
            .fetchFirst()
        );
    }

    @Override
    public boolean existeRegistroHojeParaExercicios(List<Integer> exercicioIds, Integer usuarioId, LocalDate hoje) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(registroMusculacao)
            .where(registroMusculacao.exercicio.id.in(exercicioIds)
                .and(registroMusculacao.usuario.id.eq(usuarioId))
                .and(registroMusculacao.dataCadastro.eq(hoje)))
            .fetchFirst() != null;
    }

    @Override
    public void migrarRegistrosSemVariacao(Integer exercicioId, Integer variacaoPadraoId) {
        new JPAQueryFactory(entityManager)
            .update(registroMusculacao)
            .where(registroMusculacao.exercicio.id.eq(exercicioId)
                .and(registroMusculacao.exercicioVariacao.isNull()))
            .set(registroMusculacao.exercicioVariacao.id, variacaoPadraoId)
            .execute();
    }

    @Override
    public void moverRegistros(List<Integer> registroIds, Integer variacaoDestinoId, Integer usuarioId) {
        new JPAQueryFactory(entityManager)
            .update(registroMusculacao)
            .where(registroMusculacao.id.in(registroIds)
                .and(registroMusculacao.usuario.id.eq(usuarioId)))
            .set(registroMusculacao.exercicioVariacao.id, variacaoDestinoId)
            .execute();
    }

    @Override
    public List<RegistroMusculacao> findTodosPrsPorUsuarioId(Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(registroMusculacao)
            .innerJoin(registroMusculacao.exercicio, exercicio).fetchJoin()
            .innerJoin(registroMusculacao.usuario, usuario).fetchJoin()
            .where(registroMusculacao.usuario.id.eq(usuarioId))
            .fetch();
    }
}
