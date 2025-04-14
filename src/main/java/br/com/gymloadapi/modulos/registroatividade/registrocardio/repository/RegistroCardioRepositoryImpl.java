package br.com.gymloadapi.modulos.registroatividade.registrocardio.repository;

import br.com.gymloadapi.modulos.registroatividade.registrocardio.model.RegistroCardio;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.EntityManager;
import java.util.List;

import static br.com.gymloadapi.modulos.exercicio.model.QExercicio.exercicio;
import static br.com.gymloadapi.modulos.registroatividade.registrocardio.model.QRegistroCardio.registroCardio;
import static br.com.gymloadapi.modulos.usuario.model.QUsuario.usuario;

@RequiredArgsConstructor
public class RegistroCardioRepositoryImpl implements RegistroCardioRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<RegistroCardio> findAllByExercicioIdAndUsuarioId(Integer exercicioId, Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(registroCardio)
            .innerJoin(registroCardio.exercicio, exercicio).fetchJoin()
            .innerJoin(registroCardio.usuario, usuario).fetchJoin()
            .where(registroCardio.exercicio.id.eq(exercicioId)
                .and(registroCardio.usuario.id.eq(usuarioId)))
            .fetch();
    }
}
