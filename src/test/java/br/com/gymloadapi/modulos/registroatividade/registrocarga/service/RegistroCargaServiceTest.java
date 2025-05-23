package br.com.gymloadapi.modulos.registroatividade.registrocarga.service;

import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.PermissaoException;
import br.com.gymloadapi.modulos.registroatividade.mapper.RegistroAtividadeMapper;
import br.com.gymloadapi.modulos.registroatividade.mapper.RegistroAtividadeMapperImpl;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.model.RegistroCarga;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.repository.RegistroCargaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static br.com.gymloadapi.modulos.comum.enums.EUnidadePeso.KG;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioMusculacao;
import static br.com.gymloadapi.modulos.registroatividade.helper.RegistroAtividadeHelper.umRegistroAtividadeRequestParaMusculacao;
import static br.com.gymloadapi.modulos.registroatividade.registrocarga.helper.RegistroCargaHelper.umRegistroCarga;
import static br.com.gymloadapi.modulos.registroatividade.registrocarga.helper.RegistroCargaHelper.umaListaRegistroCarga;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuario;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistroCargaServiceTest {

    private RegistroCargaService service;
    private final RegistroAtividadeMapper registroAtividadeMapper = new RegistroAtividadeMapperImpl();

    @Mock
    private RegistroCargaRepository repository;
    @Captor
    private ArgumentCaptor<RegistroCarga> historicoCargasCaptor;

    @BeforeEach
    void setUp() {
        service = new RegistroCargaService(repository, registroAtividadeMapper);
    }

    @Test
    void salvarRegistro_deveSalvarRegistroDeCarga_quandoSolicitado() {
        service.salvarRegistro(umRegistroAtividadeRequestParaMusculacao(), umExercicioMusculacao(1), umUsuarioAdmin());

        verify(repository).save(historicoCargasCaptor.capture());

        var historicoCargas = historicoCargasCaptor.getValue();

        assertAll(
            () -> assertEquals(22.5, historicoCargas.getPeso()),
            () -> assertEquals(KG, historicoCargas.getUnidadePeso()),
            () -> assertEquals(12, historicoCargas.getQtdRepeticoes()),
            () -> assertEquals(4, historicoCargas.getQtdSeries()),
            () -> assertEquals("SUPINO RETO", historicoCargas.getExercicio().getNome()),
            () -> assertEquals("Usuario Admin", historicoCargas.getUsuario().getNome())
        );
    }

    @Test
    void buscarDestaque_deveRetornarResponseComDadosNull_quandoExercicioNaoPossuirNenhumRegistroDeCargas() {
        var usuario = umUsuarioAdmin();

        when(repository.findAllByExercicioIdAndUsuarioId(1, usuario.getId())).thenReturn(emptyList());

        var response = service.buscarDestaque(1, usuario.getId());

        assertAll(
            () -> assertEquals(1, response.exercicioId()),
            () -> assertEquals("-", response.destaque()),
            () -> assertEquals("-", response.ultimaCarga()),
            () -> assertNull(response.ultimaDistancia())
        );

        verify(repository).findAllByExercicioIdAndUsuarioId(1, usuario.getId());
    }

    @Test
    void buscarDestaque_deveRetornarMaiorCargaECargasDoUltimoDiaRegistrado_quandoSolicitado() {
        var usuario = umUsuarioAdmin();

        when(repository.findAllByExercicioIdAndUsuarioId(1, usuario.getId())).thenReturn(umaListaRegistroCarga());

        var response = service.buscarDestaque(1, usuario.getId());

        assertAll(
            () -> assertEquals(1, response.exercicioId()),
            () -> assertEquals("27.2 (KG)", response.destaque()),
            () -> assertEquals("27.2 (KG)", response.ultimaCarga()),
            () -> assertNull(response.ultimaDistancia())
        );

        verify(repository).findAllByExercicioIdAndUsuarioId(1, usuario.getId());
    }

    @Test
    void buscarHistoricoRegistroCompleto_deveRetornarListaVazia_quandoExercicioNaoPossuirNenhumRegistroCargas() {
        var usuario = umUsuarioAdmin();

        when(repository.findAllByExercicioIdAndUsuarioId(1, usuario.getId())).thenReturn(emptyList());

        assertTrue(service.buscarHistoricoRegistroCompleto(1, usuario.getId()).isEmpty());

        verify(repository).findAllByExercicioIdAndUsuarioId(1, usuario.getId());
    }

    @Test
    void buscarHistoricoRegistroCompleto_deveRetornarHistoricoCompleto_quandoExercicioPossuirRegistrosCargas() {
        var usuario = umUsuarioAdmin();

        when(repository.findAllByExercicioIdAndUsuarioId(1, usuario.getId())).thenReturn(umaListaRegistroCarga());

        var responses = service.buscarHistoricoRegistroCompleto(1, usuario.getId());

        assertAll(
            () -> assertEquals("27.2 (KG)", responses.getFirst().carga()),
            () -> assertEquals(27.2, responses.getFirst().peso()),
            () -> assertEquals(KG, responses.getFirst().unidadePeso()),
            () -> assertEquals(LocalDate.of(2025, 4, 6), responses.getFirst().dataCadastro()),
            () -> assertEquals(8, responses.getFirst().qtdRepeticoes()),
            () -> assertEquals(3, responses.getFirst().qtdSeries()),

            () -> assertEquals("25.0 (KG)", responses.get(1).carga()),
            () -> assertEquals(25.0, responses.get(1).peso()),
            () -> assertEquals(KG, responses.get(1).unidadePeso()),
            () -> assertEquals(LocalDate.of(2025, 4, 5), responses.get(1).dataCadastro()),
            () -> assertEquals(8, responses.get(1).qtdRepeticoes()),
            () -> assertEquals(2, responses.get(1).qtdSeries()),

            () -> assertEquals("22.5 (KG)", responses.getLast().carga()),
            () -> assertEquals(22.5, responses.getLast().peso()),
            () -> assertEquals(KG, responses.getLast().unidadePeso()),
            () -> assertEquals(LocalDate.of(2025, 4, 4), responses.getLast().dataCadastro()),
            () -> assertEquals(12, responses.getLast().qtdRepeticoes()),
            () -> assertEquals(4, responses.getLast().qtdSeries())
        );

        verify(repository).findAllByExercicioIdAndUsuarioId(1, usuario.getId());
    }

    @Test
    void editarRegistro_deveLancarException_quandoNaoEncontrarRegistro() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        var exception = assertThrowsExactly(
            NotFoundException.class,
            () -> service.editarRegistro(1, umRegistroAtividadeRequestParaMusculacao(), umUsuarioAdmin())
        );
        assertEquals("Registro de carga não encontrado.", exception.getMessage());

        verify(repository).findById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void editarRegistro_deveLancarException_quandoUsuarioNaoForAdminNemOMesmoUsuarioQueCriouORegistro() {
        when(repository.findById(1)).thenReturn(Optional.of(umRegistroCarga()));

        var exception = assertThrowsExactly(
            PermissaoException.class,
            () -> service.editarRegistro(1, umRegistroAtividadeRequestParaMusculacao(), umUsuario())
        );
        assertEquals(
            "Apenas usuários Admin ou o próprio usuário podem alterar as informações deste registro de carga.",
            exception.getMessage()
        );

        verify(repository).findById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void editarRegistro_deveEditarRegistroCargas_quandoSolicitado() {
        when(repository.findById(1)).thenReturn(Optional.of(umRegistroCarga()));

        service.editarRegistro(1, umRegistroAtividadeRequestParaMusculacao(), umUsuarioAdmin());

        verify(repository).findById(1);
        verify(repository).save(historicoCargasCaptor.capture());

        var historicoCargas = historicoCargasCaptor.getValue();

        assertAll(
            () -> assertEquals(22.5, historicoCargas.getPeso()),
            () -> assertEquals(KG, historicoCargas.getUnidadePeso()),
            () -> assertEquals(12, historicoCargas.getQtdRepeticoes()),
            () -> assertEquals(4, historicoCargas.getQtdSeries()),
            () -> assertEquals("SUPINO RETO", historicoCargas.getExercicio().getNome()),
            () -> assertEquals("Usuario Admin", historicoCargas.getUsuario().getNome())
        );
    }
}
