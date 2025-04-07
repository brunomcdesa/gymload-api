package br.com.gymloadapi.modulos.treino.repository;

import br.com.gymloadapi.modulos.treino.model.Treino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TreinoRepository extends JpaRepository<Treino, Integer>, QuerydslPredicateExecutor<Treino>,
    TreinoRepositoryCustom {

    List<Treino> findByUsuarioId(UUID usuarioId);
}
