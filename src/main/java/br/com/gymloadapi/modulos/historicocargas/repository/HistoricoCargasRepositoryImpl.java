package br.com.gymloadapi.modulos.historicocargas.repository;

import br.com.gymloadapi.modulos.historicocargas.model.HistoricoCargas;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.EntityManager;
import java.util.List;

import static br.com.gymloadapi.modulos.exercicio.model.QExercicio.exercicio;
import static br.com.gymloadapi.modulos.grupomuscular.model.QGrupoMuscular.grupoMuscular;
import static br.com.gymloadapi.modulos.historicocargas.model.QHistoricoCargas.historicoCargas;
import static br.com.gymloadapi.modulos.usuario.model.QUsuario.usuario;

@RequiredArgsConstructor
public class HistoricoCargasRepositoryImpl implements HistoricoCargasRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<HistoricoCargas> findAllByExercicioIdAndUsuarioId(Integer exercicioId, Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(historicoCargas)
            .innerJoin(historicoCargas.exercicio, exercicio).fetchJoin()
            .innerJoin(exercicio.grupoMuscular, grupoMuscular).fetchJoin()
            .innerJoin(historicoCargas.usuario, usuario).fetchJoin()
            .where(historicoCargas.exercicio.id.eq(exercicioId)
                .and(historicoCargas.usuario.id.eq(usuarioId)))
            .fetch();
    }
}
