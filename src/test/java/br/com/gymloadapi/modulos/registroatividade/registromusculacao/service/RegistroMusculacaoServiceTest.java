package br.com.gymloadapi.modulos.registroatividade.registromusculacao.service;

import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.PermissaoException;
import br.com.gymloadapi.modulos.registroatividade.mapper.RegistroAtividadeMapper;
import br.com.gymloadapi.modulos.registroatividade.mapper.RegistroAtividadeMapperImpl;
import br.com.gymloadapi.modulos.registroatividade.registromusculacao.model.RegistroMusculacao;
import br.com.gymloadapi.modulos.registroatividade.registromusculacao.repository.RegistroMusculacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static br.com.gymloadapi.modulos.comum.enums.ETipoPegada.PRONADA;
import static br.com.gymloadapi.modulos.comum.enums.ETipoPegada.SUPINADA;
import static br.com.gymloadapi.modulos.comum.enums.EUnidadePeso.KG;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioMusculacao;
import static br.com.gymloadapi.modulos.registroatividade.helper.RegistroAtividadeHelper.umRegistroAtividadeRequestParaMusculacao;
import static br.com.gymloadapi.modulos.registroatividade.registromusculacao.helper.RegistroMusculacaoHelper.umRegistroMusculacao;
import static br.com.gymloadapi.modulos.registroatividade.registromusculacao.helper.RegistroMusculacaoHelper.umaListaRegistroMusculacao;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.*;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistroMusculacaoServiceTest {

    private RegistroMusculacaoService service;
    private final RegistroAtividadeMapper registroAtividadeMapper = new RegistroAtividadeMapperImpl();

    @Mock
    private RegistroMusculacaoRepository repository;
    @Captor
    private ArgumentCaptor<RegistroMusculacao> registroMusculacaoCaptor;

    @BeforeEach
    void setUp() {
        service = new RegistroMusculacaoService(repository, registroAtividadeMapper);
    }

    @Test
    void salvarRegistro_deveSalvarRegistroDeMusculacao_quandoSolicitado() {
        service.salvarRegistro(umRegistroAtividadeRequestParaMusculacao(), umExercicioMusculacao(1), umUsuarioAdmin());

        verify(repository).save(registroMusculacaoCaptor.capture());

        var registroMusculacao = registroMusculacaoCaptor.getValue();

        assertAll(
            () -> assertEquals(22.5, registroMusculacao.getPeso()),
            () -> assertEquals(KG, registroMusculacao.getUnidadePeso()),
            () -> assertEquals(12, registroMusculacao.getQtdRepeticoes()),
            () -> assertEquals(4, registroMusculacao.getQtdSeries()),
            () -> assertEquals("SUPINO RETO", registroMusculacao.getExercicio().getNome()),
            () -> assertEquals("Usuario Admin", registroMusculacao.getUsuario().getNome()),
            () -> assertEquals(PRONADA, registroMusculacao.getTipoPegada())
        );
    }

    @Test
    void buscarDestaque_deveRetornarResponseComDadosNull_quandoExercicioNaoPossuirNenhumRegistroDeMusculacao() {
        var usuario = umUsuarioAdmin();

        when(repository.findAllByExercicioIdAndUsuarioId(1, usuario.getId())).thenReturn(emptyList());

        var response = service.buscarDestaque(1, usuario.getId());

        assertAll(
            () -> assertEquals(1, response.exercicioId()),
            () -> assertEquals("-", response.destaque()),
            () -> assertEquals("-", response.ultimoPeso()),
            () -> assertNull(response.ultimaDistancia()),
            () -> assertNull(response.ultimaQtdMaxRepeticoes())
        );

        verify(repository).findAllByExercicioIdAndUsuarioId(1, usuario.getId());
    }

    @Test
    void buscarDestaque_deveRetornarDestaqueEUltimoPesoRegistrado_quandoSolicitado() {
        var usuario = umUsuarioAdmin();

        when(repository.findAllByExercicioIdAndUsuarioId(1, usuario.getId()))
            .thenReturn(umaListaRegistroMusculacao());

        var response = service.buscarDestaque(1, usuario.getId());

        assertAll(
            () -> assertEquals(1, response.exercicioId()),
            () -> assertEquals("27.2 (KG)", response.destaque()),
            () -> assertEquals("27.2 (KG)", response.ultimoPeso()),
            () -> assertNull(response.ultimaDistancia())
        );

        verify(repository).findAllByExercicioIdAndUsuarioId(1, usuario.getId());
    }

    @Test
    void buscarHistoricoRegistroCompleto_deveRetornarListaVazia_quandoExercicioNaoPossuirNenhumRegistroMusculacao() {
        var usuario = umUsuarioAdmin();

        when(repository.findAllByExercicioIdAndUsuarioId(1, usuario.getId())).thenReturn(emptyList());

        assertTrue(service.buscarHistoricoRegistroCompleto(1, usuario.getId()).isEmpty());

        verify(repository).findAllByExercicioIdAndUsuarioId(1, usuario.getId());
    }

    @Test
    void buscarHistoricoRegistroCompleto_deveRetornarHistoricoCompleto_quandoExercicioPossuirRegistrosMusculacao() {
        var usuario = umUsuarioAdmin();

        when(repository.findAllByExercicioIdAndUsuarioId(1, usuario.getId()))
            .thenReturn(umaListaRegistroMusculacao());

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
        assertEquals("Registro de musculação não encontrado.", exception.getMessage());

        verify(repository).findById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void editarRegistro_deveLancarException_quandoUsuarioNaoForAdminNemOMesmoUsuarioQueCriouORegistro() {
        when(repository.findById(1)).thenReturn(Optional.of(umRegistroMusculacao()));

        var exception = assertThrowsExactly(
            PermissaoException.class,
            () -> service.editarRegistro(1, umRegistroAtividadeRequestParaMusculacao(), umUsuario())
        );
        assertEquals(
            "Apenas usuários Admin ou o próprio usuário podem alterar as informações deste registro de musculação.",
            exception.getMessage()
        );

        verify(repository).findById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void editarRegistro_deveEditarRegistroMusculacao_quandoSolicitado() {
        when(repository.findById(1)).thenReturn(Optional.of(umRegistroMusculacao()));

        service.editarRegistro(1, umRegistroAtividadeRequestParaMusculacao(), umUsuarioAdmin());

        verify(repository).findById(1);
        verify(repository).save(registroMusculacaoCaptor.capture());

        var registroMusculacao = registroMusculacaoCaptor.getValue();

        assertAll(
            () -> assertEquals(22.5, registroMusculacao.getPeso()),
            () -> assertEquals(KG, registroMusculacao.getUnidadePeso()),
            () -> assertEquals(12, registroMusculacao.getQtdRepeticoes()),
            () -> assertEquals(4, registroMusculacao.getQtdSeries()),
            () -> assertEquals("SUPINO RETO", registroMusculacao.getExercicio().getNome()),
            () -> assertEquals("Usuario Admin", registroMusculacao.getUsuario().getNome()),
            () -> assertEquals(PRONADA, registroMusculacao.getTipoPegada())
        );
    }

    @Test
    void excluirRegistro_deveLancarException_quandoNaoEncontrarRegistro() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        var exception = assertThrowsExactly(
            NotFoundException.class,
            () -> service.excluirRegistro(1, umUsuario())
        );
        assertEquals("Registro de musculação não encontrado.", exception.getMessage());

        verify(repository).findById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void excluirRegistro_deveLancarException_quandoUsuarioNaoForAdminNemOMesmoUsuarioQueCriouORegistro() {
        when(repository.findById(1)).thenReturn(Optional.of(umRegistroMusculacao()));

        var exception = assertThrowsExactly(
            PermissaoException.class,
            () -> service.excluirRegistro(1, outroUsuario())
        );
        assertEquals(
            "Apenas usuários Admin ou o próprio usuário podem excluir este registro de musculação.",
            exception.getMessage()
        );

        verify(repository).findById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void excluirRegistro_deveExcluirRegistroMusculacao_quandoSolicitado() {
        var registroMusculacao = umRegistroMusculacao();
        when(repository.findById(1)).thenReturn(Optional.of(registroMusculacao));

        assertDoesNotThrow(() -> service.excluirRegistro(1, umUsuarioAdmin()));

        verify(repository).findById(1);
        verify(repository).delete(registroMusculacao);
    }

    @Test
    void repetirUltimoRegistro_deveRepetirUltimoRegistro_quandoEncontrarAlgumRegistroParaOExercicioDoUsuario() {
        when(repository.findLastByExercicioIdAndUsuarioId(1, 1)).thenReturn(Optional.of(umRegistroMusculacao()));

        service.repetirUltimoRegistro(umExercicioMusculacao(1), umUsuarioAdmin());

        verify(repository).findLastByExercicioIdAndUsuarioId(1, 1);
        verify(repository).save(registroMusculacaoCaptor.capture());

        var registroMusculacao = registroMusculacaoCaptor.getValue();
        assertAll(
            () -> assertEquals(22.5, registroMusculacao.getPeso()),
            () -> assertEquals(KG, registroMusculacao.getUnidadePeso()),
            () -> assertEquals(12, registroMusculacao.getQtdRepeticoes()),
            () -> assertEquals(4, registroMusculacao.getQtdSeries()),
            () -> assertEquals("SUPINO RETO", registroMusculacao.getExercicio().getNome()),
            () -> assertEquals("Usuario Admin", registroMusculacao.getUsuario().getNome()),
            () -> assertEquals(SUPINADA, registroMusculacao.getTipoPegada())
        );
    }

    @Test
    void repetirUltimoRegistro_deveLancarException_quandoNaoEncontrarAlgumRegistroParaOExercicioDoUsuario() {
        when(repository.findLastByExercicioIdAndUsuarioId(1, 1)).thenReturn(Optional.empty());

        var exception = assertThrowsExactly(
            NotFoundException.class,
            () -> service.repetirUltimoRegistro(umExercicioMusculacao(1), umUsuarioAdmin())
        );
        assertEquals("Você ainda não possui nenhum registro para o exercício de musculação SUPINO RETO.",
            exception.getMessage());

        verify(repository).findLastByExercicioIdAndUsuarioId(1, 1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void repetirRegistro_deveRepetirRegistro_quandoEncontrarRegistro() {
        when(repository.findById(1)).thenReturn(Optional.of(umRegistroMusculacao()));

        service.repetirRegistro(1);

        verify(repository).findById(1);
        verify(repository).save(registroMusculacaoCaptor.capture());

        var registroMusculacao = registroMusculacaoCaptor.getValue();
        assertAll(
            () -> assertEquals(22.5, registroMusculacao.getPeso()),
            () -> assertEquals(KG, registroMusculacao.getUnidadePeso()),
            () -> assertEquals(12, registroMusculacao.getQtdRepeticoes()),
            () -> assertEquals(4, registroMusculacao.getQtdSeries()),
            () -> assertEquals("SUPINO RETO", registroMusculacao.getExercicio().getNome()),
            () -> assertEquals("Usuario Admin", registroMusculacao.getUsuario().getNome()),
            () -> assertEquals(SUPINADA, registroMusculacao.getTipoPegada())
        );
    }

    @Test
    void repetirRegistro_deveLancarException_quandoNaoEncontrarRegistro() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        var exception = assertThrowsExactly(
            NotFoundException.class,
            () -> service.repetirRegistro(1)
        );
        assertEquals("Registro de musculação não encontrado.",
            exception.getMessage());

        verify(repository).findById(1);
        verifyNoMoreInteractions(repository);
    }
}
