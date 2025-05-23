package br.com.gymloadapi.modulos.exercicio.repository;

import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.EntityManager;
import java.util.List;

import static br.com.gymloadapi.modulos.exercicio.model.QExercicio.exercicio;
import static br.com.gymloadapi.modulos.grupomuscular.model.QGrupoMuscular.grupoMuscular;

@RequiredArgsConstructor
public class ExercicioRepositoryImpl implements ExercicioRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<Exercicio> findAllComplete() {
        return new JPAQueryFactory(entityManager)
            .selectFrom(exercicio)
            .leftJoin(exercicio.grupoMuscular, grupoMuscular).fetchJoin()
            .leftJoin(exercicio.treinos).fetchJoin()
            .fetch();
    }

    @Override
    public List<Exercicio> findAllCompleteByPredicate(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(exercicio)
            .leftJoin(exercicio.grupoMuscular, grupoMuscular).fetchJoin()
            .leftJoin(exercicio.treinos).fetchJoin()
            .where(predicate)
            .fetch();
    }

    @Override
    public List<Exercicio> buscarExerciciosPorTreino(Integer treinoId) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(exercicio)
            .innerJoin(exercicio.grupoMuscular, grupoMuscular).fetchJoin()
            .leftJoin(exercicio.treinos).fetchJoin()
            .where(exercicio.treinos.any().id.eq(treinoId))
            .fetch();
    }
}
