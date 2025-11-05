package br.com.gymloadapi.modulos.exercicio.repository;

import br.com.gymloadapi.modulos.exercicio.model.ExercicioVariacao;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static br.com.gymloadapi.modulos.exercicio.model.QExercicio.exercicio;
import static br.com.gymloadapi.modulos.exercicio.model.QExercicioVariacao.exercicioVariacao;
import static br.com.gymloadapi.modulos.tipovariacao.model.QTipoVariacao.tipoVariacao;

@RequiredArgsConstructor
public class ExercicioVariacaoRepositoryImpl implements ExercicioVariacaoRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<ExercicioVariacao> findAllByExercicioId(Integer exercicioId) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(exercicioVariacao)
            .innerJoin(exercicioVariacao.exercicio, exercicio).fetchJoin()
            .leftJoin(exercicioVariacao.tipoVariacao, tipoVariacao).fetchJoin()
            .where(exercicioVariacao.exercicio.id.eq(exercicioId))
            .fetch();
    }

    @Override
    public Optional<ExercicioVariacao> findCompleteById(Integer id) {
        return Optional.ofNullable(new JPAQueryFactory(entityManager)
            .selectFrom(exercicioVariacao)
            .innerJoin(exercicioVariacao.exercicio, exercicio).fetchJoin()
            .leftJoin(exercicioVariacao.tipoVariacao, tipoVariacao).fetchJoin()
            .fetchFirst());
    }
}
