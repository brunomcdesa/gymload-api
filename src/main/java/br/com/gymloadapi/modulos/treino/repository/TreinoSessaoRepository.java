package br.com.gymloadapi.modulos.treino.repository;

import br.com.gymloadapi.modulos.treino.model.TreinoSessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TreinoSessaoRepository extends JpaRepository<TreinoSessao, Integer>,
    TreinoSessaoRepositoryCustom {
}
