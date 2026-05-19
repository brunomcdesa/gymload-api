package br.com.gymloadapi.modulos.registroatividade.registrocalistenia.repository;

import br.com.gymloadapi.modulos.registroatividade.registrocalistenia.model.RegistroCalistenia;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static br.com.gymloadapi.modulos.exercicio.model.QExercicio.exercicio;
import static br.com.gymloadapi.modulos.exercicio.model.QExercicioVariacao.exercicioVariacao;
import static br.com.gymloadapi.modulos.grupomuscular.model.QGrupoMuscular.grupoMuscular;
import static br.com.gymloadapi.modulos.registroatividade.registrocalistenia.model.QRegistroCalistenia.registroCalistenia;
import static br.com.gymloadapi.modulos.usuario.model.QUsuario.usuario;

@RequiredArgsConstructor
public class RegistroCalisteniaRepositoryImpl implements RegistroCalisteniaRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<RegistroCalistenia> findAllByExercicioIdAndUsuarioId(Integer exercicioId, Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(registroCalistenia)
            .innerJoin(registroCalistenia.exercicio, exercicio).fetchJoin()
            .innerJoin(exercicio.grupoMuscular, grupoMuscular).fetchJoin()
            .innerJoin(registroCalistenia.usuario, usuario).fetchJoin()
            .where(registroCalistenia.exercicio.id.eq(exercicioId)
                .and(registroCalistenia.exercicioVariacao.isNull())
                .and(registroCalistenia.usuario.id.eq(usuarioId)))
            .fetch();
    }

    @Override
    public List<RegistroCalistenia> findAllByExercicioIdAndUsuarioIdIncluindoVariacoes(Integer exercicioId,
                                                                                        Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(registroCalistenia)
            .innerJoin(registroCalistenia.exercicio, exercicio).fetchJoin()
            .innerJoin(exercicio.grupoMuscular, grupoMuscular).fetchJoin()
            .innerJoin(registroCalistenia.usuario, usuario).fetchJoin()
            .leftJoin(registroCalistenia.exercicioVariacao, exercicioVariacao).fetchJoin()
            .where(registroCalistenia.exercicio.id.eq(exercicioId)
                .and(registroCalistenia.usuario.id.eq(usuarioId)))
            .fetch();
    }

    @Override
    public List<RegistroCalistenia> findAllByExercicioIdAndVariacaoIdAndUsuarioId(Integer exercicioId,
                                                                                   Integer variacaoId,
                                                                                   Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(registroCalistenia)
            .innerJoin(registroCalistenia.exercicio, exercicio).fetchJoin()
            .innerJoin(exercicio.grupoMuscular, grupoMuscular).fetchJoin()
            .innerJoin(registroCalistenia.usuario, usuario).fetchJoin()
            .where(registroCalistenia.exercicio.id.eq(exercicioId)
                .and(registroCalistenia.exercicioVariacao.id.eq(variacaoId))
                .and(registroCalistenia.usuario.id.eq(usuarioId)))
            .fetch();
    }

    @Override
    public Optional<RegistroCalistenia> findLastByExercicioIdAndUsuarioId(Integer exercicioId, Integer usuarioId) {
        return Optional.ofNullable(new JPAQueryFactory(entityManager)
            .selectFrom(registroCalistenia)
            .innerJoin(registroCalistenia.exercicio, exercicio).fetchJoin()
            .innerJoin(registroCalistenia.usuario, usuario).fetchJoin()
            .where(registroCalistenia.exercicio.id.eq(exercicioId)
                .and(registroCalistenia.usuario.id.eq(usuarioId)))
            .orderBy(registroCalistenia.id.desc())
            .fetchFirst()
        );
    }

    @Override
    public boolean existeRegistroHojeParaExercicios(List<Integer> exercicioIds, Integer usuarioId, LocalDate hoje) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(registroCalistenia)
            .where(registroCalistenia.exercicio.id.in(exercicioIds)
                .and(registroCalistenia.usuario.id.eq(usuarioId))
                .and(registroCalistenia.dataCadastro.eq(hoje)))
            .fetchFirst() != null;
    }

    @Override
    public void migrarRegistrosSemVariacao(Integer exercicioId, Integer variacaoPadraoId) {
        new JPAQueryFactory(entityManager)
            .update(registroCalistenia)
            .where(registroCalistenia.exercicio.id.eq(exercicioId)
                .and(registroCalistenia.exercicioVariacao.isNull()))
            .set(registroCalistenia.exercicioVariacao.id, variacaoPadraoId)
            .execute();
    }

    @Override
    public void moverRegistros(List<Integer> registroIds, Integer variacaoDestinoId, Integer usuarioId) {
        new JPAQueryFactory(entityManager)
            .update(registroCalistenia)
            .where(registroCalistenia.id.in(registroIds)
                .and(registroCalistenia.usuario.id.eq(usuarioId)))
            .set(registroCalistenia.exercicioVariacao.id, variacaoDestinoId)
            .execute();
    }

    @Override
    public List<RegistroCalistenia> findTodosPrsPorUsuarioId(Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(registroCalistenia)
            .innerJoin(registroCalistenia.exercicio, exercicio).fetchJoin()
            .innerJoin(registroCalistenia.usuario, usuario).fetchJoin()
            .leftJoin(registroCalistenia.exercicioVariacao, exercicioVariacao).fetchJoin()
            .where(registroCalistenia.usuario.id.eq(usuarioId))
            .fetch();
    }
}
