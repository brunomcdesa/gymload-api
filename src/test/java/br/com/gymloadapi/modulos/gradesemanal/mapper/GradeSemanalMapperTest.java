package br.com.gymloadapi.modulos.gradesemanal.mapper;

import org.junit.jupiter.api.Test;

import static br.com.gymloadapi.modulos.comum.enums.EAcao.CADASTRO;
import static br.com.gymloadapi.modulos.comum.enums.EDiaSemana.SEGUNDA;
import static br.com.gymloadapi.modulos.comum.enums.ESituacao.ATIVO;
import static br.com.gymloadapi.modulos.gradesemanal.helper.GradeSemanalHelper.umaGradeSemanal;
import static br.com.gymloadapi.modulos.treino.helper.TreinoHelper.umTreino;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GradeSemanalMapperTest {

    private final GradeSemanalMapper mapper = new GradeSemanalMapperImpl();

    @Test
    void mapToModel_deveFazerMapeamentoCorreto_quandoSolicitado() {
        var model = mapper.mapToModel(SEGUNDA, umTreino(ATIVO), umUsuarioAdmin());

        assertAll(
            () -> assertNull(model.getId()),
            () -> assertEquals(SEGUNDA, model.getDiaSemana()),
            () -> assertEquals(1, model.getTreino().getId()),
            () -> assertEquals(1, model.getUsuario().getId())
        );
    }

    @Test
    void mapToResponse_deveFazerMapeamentoCorreto_quandoSolicitado() {
        var response = mapper.mapToResponse(umaGradeSemanal());

        assertAll(
            () -> assertEquals(SEGUNDA, response.diaSemana()),
            () -> assertEquals(1, response.treinoId()),
            () -> assertEquals("Um Treino", response.treinoNome())
        );
    }

    @Test
    void mapToHojeResponse_deveFazerMapeamentoCorreto_quandoConcluido() {
        var response = mapper.mapToHojeResponse(umaGradeSemanal(), true);

        assertAll(
            () -> assertEquals(1, response.treinoId()),
            () -> assertEquals("Um Treino", response.treinoNome()),
            () -> assertTrue(response.concluidoHoje())
        );
    }

    @Test
    void mapToHistorico_deveFazerMapeamentoCorreto_quandoSolicitado() {
        var historico = mapper.mapToHistorico(umaGradeSemanal(), 1, CADASTRO);

        assertAll(
            () -> assertNull(historico.getId()),
            () -> assertEquals(CADASTRO, historico.getAcao()),
            () -> assertEquals(1, historico.getUsuarioCadastroId()),
            () -> assertEquals(SEGUNDA, historico.getDiaSemana()),
            () -> assertEquals(1, historico.getTreino().getId()),
            () -> assertEquals(1, historico.getUsuario().getId())
        );
    }
}
