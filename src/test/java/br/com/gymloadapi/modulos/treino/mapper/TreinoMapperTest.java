package br.com.gymloadapi.modulos.treino.mapper;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static br.com.gymloadapi.modulos.comum.enums.EAcao.CADASTRO;
import static br.com.gymloadapi.modulos.comum.enums.ESituacao.ATIVO;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umaListaDeExercicios;
import static br.com.gymloadapi.modulos.treino.helper.TreinoHelper.umTreino;
import static br.com.gymloadapi.modulos.treino.helper.TreinoHelper.umTreinoRequest;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;
import static org.junit.jupiter.api.Assertions.*;

class TreinoMapperTest {

    private final TreinoMapper mapper = new TreinoMapperImpl();

    @Test
    void mapToModel_deveFazerMapeamentoCorreto_quandoSolicitado() {
        var model = mapper.mapToModel(umTreinoRequest(), umUsuarioAdmin(), umaListaDeExercicios());

        assertAll(
            () -> assertEquals("Um Treino", model.getNome()),
            () -> assertEquals(ATIVO, model.getSituacao()),
            () -> assertEquals("Usuario Admin", model.getUsuario().getNome()),
            () -> assertEquals("SUPINO RETO", model.getExercicios().getFirst().getNome()),
            () -> assertEquals("PUXADA ALTA", model.getExercicios().getLast().getNome())
        );
    }

    @Test
    void mapToResponse_deveFazerMapeamentoCorreto_quandoSolicitado() {
        var response = mapper.mapToResponse(umTreino(ATIVO));

        assertAll(
            () -> assertEquals(1, response.id()),
            () -> assertEquals("Um Treino", response.nome()),
            () -> assertEquals(LocalDate.of(2025, 4, 6), response.dataCadastro()),
            () -> assertEquals(ATIVO, response.situacao())
        );
    }

    @Test
    void mapToHistorico_deveFazerMapeamentoCorreto_quandoSolicitado() {
        var historico = mapper.mapToHistorico(umTreino(ATIVO), 1, CADASTRO);

        assertAll(
            () -> assertNull(historico.getId()),
            () -> assertEquals(CADASTRO, historico.getAcao()),
            () -> assertEquals(1, historico.getUsuarioCadastroId()),
            () -> assertEquals(1, historico.getTreino().getId())
        );
    }
}
