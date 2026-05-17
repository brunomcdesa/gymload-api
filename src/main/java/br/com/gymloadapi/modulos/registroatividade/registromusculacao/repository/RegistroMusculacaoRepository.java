package br.com.gymloadapi.modulos.registroatividade.registromusculacao.repository;

import br.com.gymloadapi.modulos.registroatividade.registromusculacao.model.RegistroMusculacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistroMusculacaoRepository extends JpaRepository<RegistroMusculacao, Integer>,
    QuerydslPredicateExecutor<RegistroMusculacao>, RegistroMusculacaoRepositoryCustom {

    Optional<RegistroMusculacao> findFirstByExercicioVariacao_IdAndUsuario_IdOrderByIdDesc(Integer variacaoId,
                                                                                            Integer usuarioId);

    long countByExercicio_IdAndExercicioVariacaoIsNull(Integer exercicioId);

    long countByExercicioVariacao_Id(Integer variacaoId);
}
