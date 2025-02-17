package br.com.gymloadapi.modulos.grupomuscular.repository;

import br.com.gymloadapi.modulos.grupomuscular.model.GrupoMuscular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GrupoMuscularRepository extends JpaRepository<GrupoMuscular, Integer>,
        QuerydslPredicateExecutor<GrupoMuscular> {
}
