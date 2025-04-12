package br.com.gymloadapi.modulos.registroatividade.registrocarga.repository;

import br.com.gymloadapi.modulos.registroatividade.registrocarga.model.RegistroCarga;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.EntityManager;
import java.util.List;

import static br.com.gymloadapi.modulos.exercicio.model.QExercicio.exercicio;
import static br.com.gymloadapi.modulos.grupomuscular.model.QGrupoMuscular.grupoMuscular;
import static br.com.gymloadapi.modulos.registroatividade.registrocarga.model.QRegistroCarga.registroCarga;
import static br.com.gymloadapi.modulos.usuario.model.QUsuario.usuario;

@RequiredArgsConstructor
public class HistoricoCargasRepositoryImpl implements HistoricoCargasRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<RegistroCarga> findAllByExercicioIdAndUsuarioId(Integer exercicioId, Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(registroCarga)
            .innerJoin(registroCarga.exercicio, exercicio).fetchJoin()
            .innerJoin(exercicio.grupoMuscular, grupoMuscular).fetchJoin()
            .innerJoin(registroCarga.usuario, usuario).fetchJoin()
            .where(registroCarga.exercicio.id.eq(exercicioId)
                .and(registroCarga.usuario.id.eq(usuarioId)))
            .fetch();
    }
}
