package br.com.gymloadapi.modulos.treino.repository;

import java.time.LocalDate;
import java.util.List;

public interface TreinoSessaoRepositoryCustom {

    List<LocalDate> findDistinctDatesByUsuarioId(Integer usuarioId);

    List<LocalDate> findDistinctDatesByUsuarioIdBetween(Integer usuarioId, LocalDate inicio, LocalDate fim);

    long countSessoesByUsuarioIdBetween(Integer usuarioId, LocalDate inicio, LocalDate fim);
}
