package br.com.gymloadapi.modulos.treino.repository;

import br.com.gymloadapi.modulos.treino.model.TreinoCompartilhamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TreinoCompartilhamentoRepository extends JpaRepository<TreinoCompartilhamento, Integer> {

    Optional<TreinoCompartilhamento> findByCodigo(String codigo);

    Optional<TreinoCompartilhamento> findByUsuarioIdAndNomeTreinoAndExerciciosIdsAndDataExpiracaoAfter(
        Integer usuarioId, String nomeTreino, String exerciciosIds, LocalDateTime agora);
}
