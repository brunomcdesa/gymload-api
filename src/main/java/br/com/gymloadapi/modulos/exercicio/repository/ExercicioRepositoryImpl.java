package br.com.gymloadapi.modulos.exercicio.repository;

import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.EntityManager;
import java.util.List;

import static br.com.gymloadapi.modulos.exercicio.model.QExercicio.exercicio;

@RequiredArgsConstructor
public class ExercicioRepositoryImpl implements ExercicioRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<Exercicio> findAllByPredicate(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(exercicio)
            .where(predicate)
            .fetch();
    }
}
