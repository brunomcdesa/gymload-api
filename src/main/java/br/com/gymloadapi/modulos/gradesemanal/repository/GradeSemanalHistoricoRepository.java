package br.com.gymloadapi.modulos.gradesemanal.repository;

import br.com.gymloadapi.modulos.gradesemanal.model.GradeSemanalHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeSemanalHistoricoRepository extends JpaRepository<GradeSemanalHistorico, Integer> {
}
