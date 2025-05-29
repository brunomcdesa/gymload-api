package br.com.gymloadapi.modulos.registroatividade.registromusculacao.repository;

import br.com.gymloadapi.modulos.registroatividade.registromusculacao.model.RegistroMusculacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroMusculacaoRepository extends JpaRepository<RegistroMusculacao, Integer>,
    QuerydslPredicateExecutor<RegistroMusculacao>, RegistroMusculacaoRepositoryCustom {

}
