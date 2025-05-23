package br.com.gymloadapi.modulos.exercicio.mapper;

import org.junit.jupiter.api.Test;

import static br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento.HALTER;
import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.MUSCULACAO;
import static br.com.gymloadapi.modulos.comum.enums.ETipoPegada.PRONADA;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioMusculacao;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioMusculacaoRequest;
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
            () -> assertEquals(PRONADA, model.getTipoPegada()),
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
            () -> assertEquals(MUSCULACAO, response.tipoExercicio()),
            () -> assertEquals(PRONADA, response.tipoPegada()),
            () -> assertEquals(MUSCULACAO, response.tipoExercicioo())
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
}
