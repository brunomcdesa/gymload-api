package br.com.gymloadapi.modulos.gradesemanal.repository;

import br.com.gymloadapi.modulos.comum.enums.EDiaSemana;
import br.com.gymloadapi.modulos.gradesemanal.model.GradeSemanal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeSemanalRepository extends JpaRepository<GradeSemanal, Integer> {

    Optional<GradeSemanal> findByUsuarioIdAndDiaSemana(Integer usuarioId, EDiaSemana diaSemana);

    List<GradeSemanal> findByUsuarioId(Integer usuarioId);
}
