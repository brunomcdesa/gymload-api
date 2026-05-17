package br.com.gymloadapi.modulos.exercicio.repository;

import br.com.gymloadapi.modulos.exercicio.model.ExercicioVariacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExercicioVariacaoRepository extends JpaRepository<ExercicioVariacao, Integer>,
    QuerydslPredicateExecutor<ExercicioVariacao>, ExercicioVariacaoRepositoryCustom {

    boolean existsByTipoVariacao_IdAndExercicio_Id(Integer tipoVariacaoId, Integer exercicioId);

    boolean existsByNomeIgnoreCase(String nome);

    Optional<ExercicioVariacao> findByIdAndExercicio_Id(Integer id, Integer exercicioId);

    boolean existsByExercicio_Id(Integer exercicioId);

    Optional<ExercicioVariacao> findByExercicio_IdAndPadraoTrue(Integer exercicioId);
}
