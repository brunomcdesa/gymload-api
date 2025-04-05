package br.com.gymloadapi.modulos.grupomuscular.mapper;

import org.junit.jupiter.api.Test;

import static br.com.gymloadapi.modulos.grupomuscular.helper.GrupoMuscularHelper.umGrupoMuscularCostas;
import static br.com.gymloadapi.modulos.grupomuscular.helper.GrupoMuscularHelper.umGrupoMuscularRequest;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GrupoMuscularMapperTest {

    private final GrupoMuscularMapper mapper = new GrupoMuscularMapperImpl();

    @Test
    void mapToModel_deveFazerOMapeamentoCorreto_quandoSolicitado() {
        var model = mapper.mapToModel(umGrupoMuscularRequest());

        assertAll(
            () -> assertEquals("Peitoral", model.getNome()),
            () -> assertEquals("PEITORAL", model.getCodigo())
        );
    }

    @Test
    void mapToSelectResponse_deveFazerOMapeamentoCorreto_quandoSolicitado() {
        var selectResponse = mapper.mapToSelectResponse(umGrupoMuscularCostas());

        assertAll(
            () -> assertEquals(2, selectResponse.value()),
            () -> assertEquals("Costas", selectResponse.label())
        );
    }

    @Test
    void mapToResponse_deveFazerOMapeamentoCorreto_quandoSolicitado() {
        var response = mapper.mapToResponse(umGrupoMuscularCostas());

        assertAll(
            () -> assertEquals(2, response.id()),
            () -> assertEquals("Costas", response.nome())
        );
    }
}
