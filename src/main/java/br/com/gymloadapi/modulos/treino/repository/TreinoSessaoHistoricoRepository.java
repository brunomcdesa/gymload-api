package br.com.gymloadapi.modulos.treino.repository;

import br.com.gymloadapi.modulos.treino.model.TreinoSessaoHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TreinoSessaoHistoricoRepository extends JpaRepository<TreinoSessaoHistorico, Integer> {
}
