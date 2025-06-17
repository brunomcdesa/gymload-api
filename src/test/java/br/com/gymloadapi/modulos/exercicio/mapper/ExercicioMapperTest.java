package br.com.gymloadapi.modulos.exercicio.mapper;

import org.junit.jupiter.api.Test;

import static br.com.gymloadapi.modulos.comum.enums.EAcao.CADASTRO;
import static br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento.HALTER;
import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.MUSCULACAO;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.*;
import static br.com.gymloadapi.modulos.grupomuscular.helper.GrupoMuscularHelper.umGrupoMuscularPeitoral;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExercicioMapperTest {

    private final ExercicioMapper mapper = new ExercicioMapperImpl();

    @Test
    void mapToModel_deveFazerOMapeamentoCorreto_quandoSolicitado() {
        var model = mapper.mapToModel(umExercicioMusculacaoRequest(), umGrupoMuscularPeitoral());

        assertAll(
            () -> assertEquals("SUPINO RETO", model.getNome()),
            () -> assertEquals("Supino Reto", model.getDescricao()),
            () -> assertEquals(HALTER, model.getTipoEquipamento()),
            () -> assertEquals(1, model.getGrupoMuscular().getId()),
            () -> assertEquals("Peitoral", model.getGrupoMuscular().getNome())
        );
    }

    @Test
    void mapModelToResponse_deveFazerOMapeamentoCorreto_quandoSolicitado() {
        var response = mapper.mapModelToResponse(umExercicioMusculacao(1));

        assertAll(
            () -> assertEquals(1, response.id()),
            () -> assertEquals("SUPINO RETO", response.nome()),
            () -> assertEquals("Supino Reto", response.descricao()),
            () -> assertEquals("Peitoral", response.grupoMuscularNome()),
            () -> assertEquals(MUSCULACAO, response.tipoExercicio())
        );
    }

    @Test
    void mapToSelectResponse_deveFazerOMapeamentoCorreto_quandoSolicitado() {
        var selectResponse = mapper.mapToSelectResponse(umExercicioMusculacao(1));

        assertAll(
            () -> assertEquals(1, selectResponse.value()),
            () -> assertEquals("SUPINO RETO (HALTER)", selectResponse.label())
        );
    }

    @Test
    void editar_deveAlterarOsDados_quandoSolicitado() {
        var exercicio = outroExercicioMusculacao(1);
        mapper.editar(umExercicioMusculacaoRequest(), umGrupoMuscularPeitoral(), exercicio);

        assertAll(
            () -> assertEquals("SUPINO RETO", exercicio.getNome()),
            () -> assertEquals("Supino Reto", exercicio.getDescricao()),
            () -> assertEquals(HALTER, exercicio.getTipoEquipamento()),
            () -> assertEquals(1, exercicio.getGrupoMuscular().getId()),
            () -> assertEquals("Peitoral", exercicio.getGrupoMuscular().getNome())
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
}
