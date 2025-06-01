package br.com.gymloadapi.modulos.registroatividade.registrocalistenia.repository;

import br.com.gymloadapi.modulos.registroatividade.registrocalistenia.model.RegistroCalistenia;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static br.com.gymloadapi.modulos.exercicio.model.QExercicio.exercicio;
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
}
