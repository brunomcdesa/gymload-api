package br.com.gymloadapi.modulos.tipovariacao.repository;

import br.com.gymloadapi.modulos.tipovariacao.model.TipoVariacaoHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoVariacaoHistoricoRepository extends JpaRepository<TipoVariacaoHistorico, Integer>,
    QuerydslPredicateExecutor<TipoVariacaoHistorico> {
}
