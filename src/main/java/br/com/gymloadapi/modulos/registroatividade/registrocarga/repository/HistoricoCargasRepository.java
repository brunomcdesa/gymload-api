package br.com.gymloadapi.modulos.registroatividade.registrocarga.repository;

import br.com.gymloadapi.modulos.registroatividade.registrocarga.model.RegistroCarga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricoCargasRepository extends JpaRepository<RegistroCarga, Integer>,
    QuerydslPredicateExecutor<RegistroCarga>, HistoricoCargasRepositoryCustom {

}
