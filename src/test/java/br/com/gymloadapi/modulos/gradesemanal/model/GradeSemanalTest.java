package br.com.gymloadapi.modulos.gradesemanal.model;

import br.com.gymloadapi.modulos.comum.enums.ESituacao;
import org.junit.jupiter.api.Test;

import static br.com.gymloadapi.modulos.gradesemanal.helper.GradeSemanalHelper.umaGradeSemanal;
import static br.com.gymloadapi.modulos.treino.helper.TreinoHelper.umTreinoImportado;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GradeSemanalTest {

    @Test
    void alterarTreino_deveSubstituirOTreinoDaGrade_quandoSolicitado() {
        var grade = umaGradeSemanal();
        var novoTreino = umTreinoImportado(ESituacao.ATIVO);

        grade.alterarTreino(novoTreino);

        assertAll(
            () -> assertEquals(novoTreino, grade.getTreino()),
            () -> assertEquals(2, grade.getTreino().getId())
        );
    }
}
