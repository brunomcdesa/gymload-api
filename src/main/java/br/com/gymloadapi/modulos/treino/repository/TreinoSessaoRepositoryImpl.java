package br.com.gymloadapi.modulos.treino.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static br.com.gymloadapi.modulos.treino.model.QTreinoSessao.treinoSessao;

@RequiredArgsConstructor
public class TreinoSessaoRepositoryImpl implements TreinoSessaoRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<LocalDate> findDistinctDatesByUsuarioId(Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
            .select(treinoSessao.dataSessao)
            .distinct()
            .from(treinoSessao)
            .where(treinoSessao.usuario.id.eq(usuarioId))
            .orderBy(treinoSessao.dataSessao.desc())
            .fetch();
    }

    @Override
    public List<LocalDate> findDistinctDatesByUsuarioIdBetween(Integer usuarioId, LocalDate inicio, LocalDate fim) {
        return new JPAQueryFactory(entityManager)
            .select(treinoSessao.dataSessao)
            .distinct()
            .from(treinoSessao)
            .where(treinoSessao.usuario.id.eq(usuarioId)
                .and(treinoSessao.dataSessao.between(inicio, fim)))
            .fetch();
    }

    @Override
    public long countSessoesByUsuarioIdBetween(Integer usuarioId, LocalDate inicio, LocalDate fim) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(treinoSessao)
            .where(treinoSessao.usuario.id.eq(usuarioId)
                .and(treinoSessao.dataSessao.between(inicio, fim)))
            .fetchCount();
    }
}
