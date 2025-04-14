package br.com.gymloadapi.modulos.registroatividade.registrocardio.repository;

import br.com.gymloadapi.modulos.registroatividade.registrocardio.model.RegistroCardio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroCardioRepository extends JpaRepository<RegistroCardio, Integer>,
    QuerydslPredicateExecutor<RegistroCardio>, RegistroCardioRepositoryCustom {
}
