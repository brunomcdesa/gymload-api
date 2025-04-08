package br.com.gymloadapi.modulos.historicocargas.repository;

import br.com.gymloadapi.modulos.historicocargas.model.HistoricoCargas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricoCargasRepository extends JpaRepository<HistoricoCargas, Integer>,
    QuerydslPredicateExecutor<HistoricoCargas>, HistoricoCargasRepositoryCustom {

}
