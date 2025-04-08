package br.com.gymloadapi.modulos.historicocargas.repository;

import br.com.gymloadapi.modulos.historicocargas.model.HistoricoCargas;

import java.util.List;
import java.util.UUID;

public interface HistoricoCargasRepositoryCustom {

    List<HistoricoCargas> findAllByExercicioIdAndUsuarioId(Integer exercicioId, UUID usuarioId);
}
