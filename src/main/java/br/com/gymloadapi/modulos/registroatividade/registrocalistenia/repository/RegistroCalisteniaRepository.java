package br.com.gymloadapi.modulos.registroatividade.registrocalistenia.repository;

import br.com.gymloadapi.modulos.registroatividade.registrocalistenia.model.RegistroCalistenia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistroCalisteniaRepository extends JpaRepository<RegistroCalistenia, Integer>,
    QuerydslPredicateExecutor<RegistroCalistenia>, RegistroCalisteniaRepositoryCustom {

    Optional<RegistroCalistenia> findFirstByExercicioVariacao_IdAndUsuario_IdOrderByIdDesc(Integer variacaoId,
                                                                                            Integer usuarioId);
}
