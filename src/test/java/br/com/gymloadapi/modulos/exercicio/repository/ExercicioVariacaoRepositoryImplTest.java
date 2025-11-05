package br.com.gymloadapi.modulos.exercicio.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ExercicioVariacaoRepositoryImplTest {

    @Autowired
    private ExercicioVariacaoRepository repository;

    @Test
    void findAllByExercicioId_deveRetornarAsVariacoesDoExercicio_quandoSolicitado() {
        var exercicioVariacoes = repository.findAllByExercicioId(1);
        assertAll(
            () -> assertEquals(1, exercicioVariacoes.getFirst().getId()),
            () -> assertEquals("SUPINO RETO - HALTER", exercicioVariacoes.getFirst().getNome()),
            () -> assertEquals(2, exercicioVariacoes.get(1).getId()),
            () -> assertEquals("SUPINO RETO - BARRA", exercicioVariacoes.get(1).getNome()),
            () -> assertEquals(3, exercicioVariacoes.getLast().getId()),
            () -> assertEquals("SUPINO RETO - MAQUINA", exercicioVariacoes.getLast().getNome())
        );
    }

    @Test
    void findAllByExercicioId_deveRetornarListaVazia_quandoNaoEncontrarVariacoesParaOExercicio() {
        assertTrue(repository.findAllByExercicioId(3).isEmpty());
    }

    @Test
    void findCompleteById_deveRetornarOptionalPreenchido_quandoEncontrarVariacaoDeExercicio() {
        var exercicioVariacao = repository.findCompleteById(1);
        assertAll(
            () -> assertTrue(exercicioVariacao.isPresent()),
            () -> assertEquals(1, exercicioVariacao.get().getId()),
            () -> assertEquals("SUPINO RETO - HALTER", exercicioVariacao.get().getNome()),
            () -> assertEquals(1, exercicioVariacao.get().getExercicio().getId()),
            () -> assertEquals(1, exercicioVariacao.get().getTipoVariacao().getId())
        );
    }
}
