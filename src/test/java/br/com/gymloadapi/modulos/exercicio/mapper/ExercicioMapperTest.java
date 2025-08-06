package br.com.gymloadapi.modulos.exercicio.mapper;

import org.junit.jupiter.api.Test;

import static br.com.gymloadapi.modulos.comum.enums.EAcao.CADASTRO;
import static br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento.HALTER;
import static br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento.POLIA;
import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.MUSCULACAO;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.*;
import static br.com.gymloadapi.modulos.grupomuscular.helper.GrupoMuscularHelper.umGrupoMuscularPeitoral;
import static org.junit.jupiter.api.Assertions.*;

class ExercicioMapperTest {

    private final ExercicioMapper mapper = new ExercicioMapperImpl();

    @Test
    void mapToModel_deveFazerOMapeamentoCorreto_quandoSolicitado() {
        var model = mapper.mapToModel(umExercicioMusculacaoRequest(), umGrupoMuscularPeitoral());

        assertAll(
            () -> assertEquals("SUPINO RETO", model.getNome()),
            () -> assertEquals("Supino Reto", model.getDescricao()),
            () -> assertEquals(1, model.getGrupoMuscular().getId()),
            () -> assertEquals("Peitoral", model.getGrupoMuscular().getNome()),
            () -> assertTrue(model.getPossuiVariacao())
        );
    }

    @Test
    void mapModelToResponse_deveFazerOMapeamentoCorreto_quandoSolicitado() {
        var response = mapper.mapModelToResponse(umExercicioMusculacao(1));

        assertAll(
            () -> assertEquals(1, response.id()),
            () -> assertEquals("SUPINO RETO", response.nome()),
            () -> assertEquals("Supino Reto", response.descricao()),
            () -> assertEquals(1, response.grupoMuscularId()),
            () -> assertEquals("Peitoral", response.grupoMuscularNome()),
            () -> assertEquals(MUSCULACAO, response.tipoExercicio())
        );
    }

    @Test
    void mapToSelectResponse_deveFazerOMapeamentoCorreto_quandoSolicitado() {
        var selectResponse = mapper.mapToSelectResponse(umExercicioMusculacao(1));

        assertAll(
            () -> assertEquals(1, selectResponse.value()),
            () -> assertEquals("SUPINO RETO", selectResponse.label())
        );
    }

    @Test
    void editar_deveAlterarOsDados_quandoSolicitado() {
        var exercicio = outroExercicioMusculacao(1);
        mapper.editar(umExercicioMusculacaoRequest(), umGrupoMuscularPeitoral(), exercicio);

        assertAll(
            () -> assertEquals("SUPINO RETO", exercicio.getNome()),
            () -> assertEquals("Supino Reto", exercicio.getDescricao()),
            () -> assertEquals(1, exercicio.getGrupoMuscular().getId()),
            () -> assertEquals("Peitoral", exercicio.getGrupoMuscular().getNome()),
            () -> assertTrue(exercicio.getPossuiVariacao())
        );
    }

    @Test
    void mapToHistorico_deveRetornarHistorico_quandoSolicitado() {
        var historico = mapper.mapToHistorico(umExercicioMusculacao(1), 1, CADASTRO);

        assertAll(
            () -> assertEquals("SUPINO RETO", historico.getExercicio().getNome()),
            () -> assertEquals(1, historico.getExercicio().getId()),
            () -> assertEquals(CADASTRO, historico.getAcao()),
            () -> assertEquals(1, historico.getUsuarioCadastroId())
        );
    }

    @Test
    void mapToExercicioVariacao_deveRetornarExercicioVariacao_quandoSolicitado() {
        var variacao = mapper.mapToExercicioVariacao(umExercicioMusculacao(1), 2,
            POLIA, "SUPINO RETO - Polia");

        assertAll(
            () -> assertEquals("SUPINO RETO - Polia", variacao.getNome()),
            () -> assertEquals(1, variacao.getExercicio().getId()),
            () -> assertEquals(POLIA, variacao.getTipoEquipamento()),
            () -> assertEquals(2, variacao.getUsuarioCadastroId())
        );
    }

    @Test
    void mapToExercicioVariacaoResponse_deveRetornarExercicioVariacaoResponse_quandoSolicitado() {
        var response = mapper.mapToExercicioVariacaoResponse(umExercicioVariacao());

        assertAll(
            () -> assertEquals(1, response.id()),
            () -> assertEquals("SUPINO RETO - Halter", response.nome()),
            () -> assertEquals(HALTER, response.tipoEquipamento())
        );
    }
}
