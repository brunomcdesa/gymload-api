package br.com.gymloadapi.modulos.treino.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static br.com.gymloadapi.modulos.comum.enums.ESituacao.ATIVO;
import static br.com.gymloadapi.modulos.comum.enums.ESituacao.INATIVO;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.outraListaDeExercicios;
import static br.com.gymloadapi.modulos.treino.helper.TreinoHelper.umTreino;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TreinoTest {

    @Test
    void getExerciciosIds_deveRetornarListaDeIdsDosExercicios_quandoSolicitado() {
        assertEquals(List.of(1, 2), umTreino(ATIVO).getExerciciosIds());
    }

    @Test
    void alterarSituacao_deveAlterarSituacaoDoTreinoParaAtivo_quandoTreinoEstiverInativo() {
        var treino = umTreino(INATIVO);
        treino.alterarSituacao();
        assertEquals(ATIVO, treino.getSituacao());
    }

    @Test
    void alterarSituacao_deveAlterarSituacaoDoTreinoParaInativo_quandoTreinoEstiverAtivo() {
        var treino = umTreino(ATIVO);
        treino.alterarSituacao();
        assertEquals(INATIVO, treino.getSituacao());
    }

    @Test
    void alterarDados_deveAlterarOsDados_quandoSolicitado() {
        var treino = umTreino(ATIVO);
        treino.alterarDados("editado", outraListaDeExercicios());

        assertAll(
            () -> assertEquals("editado", treino.getNome()),
            () -> assertEquals(3, treino.getExercicios().getFirst().getId()),
            () -> assertEquals(4, treino.getExercicios().getLast().getId())
        );
    }
}
