package br.com.gymloadapi.modulos.treino.repository;

import br.com.gymloadapi.modulos.treino.model.Treino;

import java.util.Optional;

public interface TreinoRepositoryCustom {

    Optional<Treino> findCompleteById(Integer id);
}
