package br.com.gymloadapi.modulos.treino.repository;

import br.com.gymloadapi.modulos.treino.model.TreinoHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TreinoHistoricoRepository extends JpaRepository<TreinoHistorico, Integer>,
    QuerydslPredicateExecutor<TreinoHistorico> {
}
