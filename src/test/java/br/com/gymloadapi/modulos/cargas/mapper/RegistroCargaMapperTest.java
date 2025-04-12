package br.com.gymloadapi.modulos.cargas.mapper;

import br.com.gymloadapi.modulos.registroatividade.registrocarga.mapper.HistoricoCargasMapper;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.mapper.HistoricoCargasMapperImpl;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static br.com.gymloadapi.modulos.cargas.helper.RegistroCargaHelper.umRegistroCarga;
import static br.com.gymloadapi.modulos.cargas.helper.RegistroCargaHelper.umHistoricoCargasRequest;
import static br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento.HALTER;
import static br.com.gymloadapi.modulos.comum.enums.EUnidadePeso.KG;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicio;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RegistroCargaMapperTest {

    private final HistoricoCargasMapper mapper = new HistoricoCargasMapperImpl();

    @Test
    void mapToModel_deveFazerOMapeamentoCorreto_quandoSolicitado() {
        var model = mapper.mapToModel(umHistoricoCargasRequest(), umExercicio(1), umUsuarioAdmin());

        assertAll(
            () -> assertEquals(22.5, model.getPeso()),
            () -> assertEquals(KG, model.getUnidadePeso()),
            () -> assertEquals(12, model.getQtdRepeticoes()),
            () -> assertEquals(4, model.getQtdSeries()),
            () -> assertEquals("SUPINO RETO", model.getExercicio().getNome()),
            () -> assertEquals("Usuario Admin", model.getUsuario().getNome())
        );
    }

    @Test
    void mapToResponse_deveFazerOMapeamentoCorreto_quandoSolicitado() {
        var response = mapper.mapToResponse(umRegistroCarga());

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
