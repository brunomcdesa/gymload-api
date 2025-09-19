package br.com.gymloadapi.modulos.tipoVariacao.repository;

import br.com.gymloadapi.modulos.tipoVariacao.model.TipoVariacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoVariacaoRepository extends JpaRepository<TipoVariacao, Integer>,
    QuerydslPredicateExecutor<TipoVariacao> {

    boolean existsByNomeIgnoreCase(String nome);
}
