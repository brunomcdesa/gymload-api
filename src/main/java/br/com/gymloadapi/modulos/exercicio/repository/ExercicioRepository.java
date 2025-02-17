package br.com.gymloadapi.modulos.exercicio.repository;

import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import com.querydsl.core.types.Predicate;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExercicioRepository extends JpaRepository<Exercicio, Integer>, QuerydslPredicateExecutor<Exercicio> {

    List<Exercicio> findAllBy(Predicate predicate);
}
