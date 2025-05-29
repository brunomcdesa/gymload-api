package br.com.gymloadapi.modulos.registroatividade.registromusculacao.repository;

import br.com.gymloadapi.modulos.registroatividade.registromusculacao.model.RegistroMusculacao;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.EntityManager;
import java.util.List;

import static br.com.gymloadapi.modulos.exercicio.model.QExercicio.exercicio;
import static br.com.gymloadapi.modulos.grupomuscular.model.QGrupoMuscular.grupoMuscular;
import static br.com.gymloadapi.modulos.registroatividade.registromusculacao.model.QRegistroMusculacao.registroMusculacao;
import static br.com.gymloadapi.modulos.usuario.model.QUsuario.usuario;

@RequiredArgsConstructor
public class RegistroMusculacaoRepositoryImpl implements RegistroMusculacaoRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<RegistroMusculacao> findAllByExercicioIdAndUsuarioId(Integer exercicioId, Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(registroMusculacao)
            .innerJoin(registroMusculacao.exercicio, exercicio).fetchJoin()
            .innerJoin(exercicio.grupoMuscular, grupoMuscular).fetchJoin()
            .innerJoin(registroMusculacao.usuario, usuario).fetchJoin()
            .where(registroMusculacao.exercicio.id.eq(exercicioId)
                .and(registroMusculacao.usuario.id.eq(usuarioId)))
            .fetch();
    }
}
