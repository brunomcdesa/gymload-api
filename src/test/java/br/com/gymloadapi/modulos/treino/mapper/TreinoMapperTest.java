package br.com.gymloadapi.modulos.treino.mapper;

import br.com.gymloadapi.modulos.exercicio.dto.ExercicioResponse;
import br.com.gymloadapi.modulos.comum.enums.ETipoExercicio;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static br.com.gymloadapi.modulos.comum.enums.EAcao.CADASTRO;
import static br.com.gymloadapi.modulos.comum.enums.ESituacao.ATIVO;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umaListaDeExercicios;
import static br.com.gymloadapi.modulos.treino.helper.TreinoHelper.*;
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
            () -> assertFalse(model.getImportado()),
            () -> assertEquals("Usuario Admin", model.getUsuario().getNome()),
            () -> assertEquals("SUPINO RETO", model.getExercicios().getFirst().getNome()),
            () -> assertEquals("PUXADA ALTA", model.getExercicios().getLast().getNome())
        );
    }

    @Test
    void mapCompartilhadoToModel_deveFazerMapeamentoCorreto_quandoSolicitado() {
        var model = mapper.mapCompartilhadoToModel("Treino Importado", umUsuarioAdmin(), umaListaDeExercicios());

        assertAll(
            () -> assertEquals("Treino Importado", model.getNome()),
            () -> assertEquals(ATIVO, model.getSituacao()),
            () -> assertTrue(model.getImportado()),
            () -> assertEquals("Usuario Admin", model.getUsuario().getNome()),
            () -> assertEquals("SUPINO RETO", model.getExercicios().getFirst().getNome())
        );
    }

    @Test
    void mapToResponse_deveFazerMapeamentoCorreto_quandoSolicitado() {
        var response = mapper.mapToResponse(umTreino(ATIVO));

        assertAll(
            () -> assertEquals(1, response.id()),
            () -> assertEquals("Um Treino", response.nome()),
            () -> assertEquals(LocalDate.of(2025, 4, 6), response.dataCadastro()),
            () -> assertEquals(ATIVO, response.situacao()),
            () -> assertFalse(response.importado())
        );
    }

    @Test
    void mapToCompartilhadoResponse_deveFazerMapeamentoCorreto_quandoSolicitado() {
        var exercicios = List.of(
            new ExercicioResponse(1, "SUPINO RETO", "Supino Reto", 1, "Peitoral", ETipoExercicio.MUSCULACAO, false)
        );
        var response = mapper.mapToCompartilhadoResponse(umTreinoCompartilhamento(), exercicios);

        assertAll(
            () -> assertEquals("Um Treino", response.nomeTreino()),
            () -> assertEquals(1, response.exercicios().size()),
            () -> assertEquals("SUPINO RETO", response.exercicios().getFirst().nome()),
            () -> assertEquals(LocalDate.of(2099, 12, 31), response.dataExpiracao())
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
