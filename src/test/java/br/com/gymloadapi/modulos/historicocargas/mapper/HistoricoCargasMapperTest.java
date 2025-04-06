package br.com.gymloadapi.modulos.historicocargas.mapper;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.HALTER;
import static br.com.gymloadapi.modulos.comum.enums.EUnidadePeso.KG;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicio;
import static br.com.gymloadapi.modulos.historicocargas.helper.HistoricoCargasHelper.umHistoricoCargas;
import static br.com.gymloadapi.modulos.historicocargas.helper.HistoricoCargasHelper.umHistoricoCargasRequest;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoricoCargasMapperTest {

    private final HistoricoCargasMapper mapper = new HistoricoCargasMapperImpl();

    @Test
    void mapToModel_deveFazerOMapeamentoCorreto_quandoSolicitado() {
        var model = mapper.mapToModel(umHistoricoCargasRequest(), umExercicio(1), umUsuarioAdmin());

        assertAll(
            () -> assertEquals(22.5, model.getCarga()),
            () -> assertEquals(KG, model.getUnidadePeso()),
            () -> assertEquals(12, model.getQtdRepeticoes()),
            () -> assertEquals(4, model.getQtdSeries()),
            () -> assertEquals("SUPINO RETO", model.getExercicio().getNome()),
            () -> assertEquals("Usuario Admin", model.getUsuario().getNome())
        );
    }

    @Test
    void mapToResponse_deveFazerOMapeamentoCorreto_quandoSolicitado() {
        var response = mapper.mapToResponse(umHistoricoCargas());

        assertAll(
            () -> assertEquals(1, response.id()),
            () -> assertEquals("SUPINO RETO", response.exercicioNome()),
            () -> assertEquals("22.5 (KG)", response.carga()),
            () -> assertEquals(HALTER, response.tipoExercicio()),
            () -> assertEquals("Peitoral", response.grupoMuscularNome()),
            () -> assertEquals(12, response.qtdRepeticoes()),
            () -> assertEquals(4, response.qtdSeries()),
            () -> assertEquals(LocalDate.of(2025, 4, 4), response.dataCadastro())
        );
    }
}
