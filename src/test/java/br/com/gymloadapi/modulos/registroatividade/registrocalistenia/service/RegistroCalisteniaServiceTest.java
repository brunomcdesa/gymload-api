package br.com.gymloadapi.modulos.registroatividade.registrocalistenia.service;

import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.PermissaoException;
import br.com.gymloadapi.modulos.registroatividade.mapper.RegistroAtividadeMapper;
import br.com.gymloadapi.modulos.registroatividade.mapper.RegistroAtividadeMapperImpl;
import br.com.gymloadapi.modulos.registroatividade.registrocalistenia.model.RegistroCalistenia;
import br.com.gymloadapi.modulos.registroatividade.registrocalistenia.repository.RegistroCalisteniaRepository;
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
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioCalistenia;
import static br.com.gymloadapi.modulos.registroatividade.helper.RegistroAtividadeHelper.umRegistroAtividadeRequestParaCalistenia;
import static br.com.gymloadapi.modulos.registroatividade.registrocalistenia.helper.RegistroCalisteniaHelper.*;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.*;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistroCalisteniaServiceTest {

    private RegistroCalisteniaService service;
    private final RegistroAtividadeMapper registroAtividadeMapper = new RegistroAtividadeMapperImpl();

    @Mock
    private RegistroCalisteniaRepository repository;
    @Captor
    private ArgumentCaptor<RegistroCalistenia> registroCalisteniaCaptor;

    @BeforeEach
    void setUp() {
        service = new RegistroCalisteniaService(repository, registroAtividadeMapper);
    }

    @Test
    void salvarRegistro_deveSalvarRegistroDeCalistenia_quandoSolicitado() {
        service.salvarRegistro(umRegistroAtividadeRequestParaCalistenia(), umExercicioCalistenia(1), umUsuarioAdmin());

        verify(repository).save(registroCalisteniaCaptor.capture());

        var registroCalistenia = registroCalisteniaCaptor.getValue();

        assertAll(
            () -> assertNull(registroCalistenia.getPesoAdicional()),
            () -> assertNull(registroCalistenia.getUnidadePeso()),
            () -> assertEquals(20, registroCalistenia.getQtdRepeticoes()),
            () -> assertEquals(5, registroCalistenia.getQtdSeries()),
            () -> assertEquals("Abdominal Supra", registroCalistenia.getExercicio().getNome()),
            () -> assertEquals("Usuario Admin", registroCalistenia.getUsuario().getNome())
        );
    }

    @Test
    void buscarDestaque_deveRetornarResponseComDadosNull_quandoExercicioNaoPossuirNenhumRegistroDeCalistenia() {
        var usuario = umUsuarioAdmin();

        when(repository.findAllByExercicioIdAndUsuarioId(1, usuario.getId())).thenReturn(emptyList());

        var response = service.buscarDestaque(1, usuario.getId());

        assertAll(
            () -> assertEquals(1, response.exercicioId()),
            () -> assertEquals("-", response.destaque()),
            () -> assertNull(response.ultimoPeso()),
            () -> assertNull(response.ultimaDistancia()),
            () -> assertNull(response.ultimaQtdMaxRepeticoes())
        );

        verify(repository).findAllByExercicioIdAndUsuarioId(1, usuario.getId());
    }

    @Test
    void buscarDestaque_deveRetornarDestaqueEUltimaQtdRepeticoesMaxima_quandoSolicitado() {
        var usuario = umUsuarioAdmin();

        when(repository.findAllByExercicioIdAndUsuarioId(1, usuario.getId()))
            .thenReturn(umaListaRegistroCalistenia());

        var response = service.buscarDestaque(1, usuario.getId());

        assertAll(
            () -> assertEquals(1, response.exercicioId()),
            () -> assertEquals("30 reps", response.destaque()),
            () -> assertNull(response.ultimoPeso()),
            () -> assertNull(response.ultimaDistancia()),
            () -> assertEquals(20, response.ultimaQtdMaxRepeticoes())
        );

        verify(repository).findAllByExercicioIdAndUsuarioId(1, usuario.getId());
    }

    @Test
    void buscarHistoricoRegistroCompleto_deveRetornarListaVazia_quandoExercicioNaoPossuirNenhumRegistroCalistenia() {
        var usuario = umUsuarioAdmin();

        when(repository.findAllByExercicioIdAndUsuarioId(1, usuario.getId())).thenReturn(emptyList());

        assertTrue(service.buscarHistoricoRegistroCompleto(1, usuario.getId()).isEmpty());

        verify(repository).findAllByExercicioIdAndUsuarioId(1, usuario.getId());
    }

    @Test
    void buscarHistoricoRegistroCompleto_deveRetornarHistoricoCompleto_quandoExercicioPossuirRegistrosCalistenia() {
        var usuario = umUsuarioAdmin();

        when(repository.findAllByExercicioIdAndUsuarioId(1, usuario.getId()))
            .thenReturn(umaListaRegistroCalistenia());

        var responses = service.buscarHistoricoRegistroCompleto(1, usuario.getId());

        assertAll(
            () -> assertEquals("15.2 (KG)", responses.getFirst().carga()),
            () -> assertEquals(15.2, responses.getFirst().peso()),
            () -> assertEquals(KG, responses.getFirst().unidadePeso()),
            () -> assertEquals(LocalDate.of(2025, 4, 6), responses.getFirst().dataCadastro()),
            () -> assertEquals(20, responses.getFirst().qtdRepeticoes()),
            () -> assertEquals(4, responses.getFirst().qtdSeries()),

            () -> assertEquals("10.0 (KG)", responses.get(1).carga()),
            () -> assertEquals(10, responses.get(1).peso()),
            () -> assertEquals(KG, responses.get(1).unidadePeso()),
            () -> assertEquals(LocalDate.of(2025, 4, 5), responses.get(1).dataCadastro()),
            () -> assertEquals(15, responses.get(1).qtdRepeticoes()),
            () -> assertEquals(5, responses.get(1).qtdSeries()),

            () -> assertNull(responses.getLast().carga()),
            () -> assertNull(responses.getLast().peso()),
            () -> assertNull(responses.getLast().unidadePeso()),
            () -> assertEquals(LocalDate.of(2025, 4, 4), responses.getLast().dataCadastro()),
            () -> assertEquals(30, responses.getLast().qtdRepeticoes()),
            () -> assertEquals(4, responses.getLast().qtdSeries())
        );

        verify(repository).findAllByExercicioIdAndUsuarioId(1, usuario.getId());
    }

    @Test
    void editarRegistro_deveLancarException_quandoNaoEncontrarRegistro() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        var exception = assertThrowsExactly(
            NotFoundException.class,
            () -> service.editarRegistro(1, umRegistroAtividadeRequestParaCalistenia(), umUsuarioAdmin())
        );
        assertEquals("Registro de calistenia não encontrado.", exception.getMessage());

        verify(repository).findById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void editarRegistro_deveLancarException_quandoUsuarioNaoForAdminNemOMesmoUsuarioQueCriouORegistro() {
        when(repository.findById(1)).thenReturn(Optional.of(umRegistroCalistenia()));

        var exception = assertThrowsExactly(
            PermissaoException.class,
            () -> service.editarRegistro(1, umRegistroAtividadeRequestParaCalistenia(), umUsuario())
        );
        assertEquals(
            "Apenas usuários Admin ou o próprio usuário podem alterar as informações deste registro de calistenia.",
            exception.getMessage()
        );

        verify(repository).findById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void editarRegistro_deveEditarRegistroMusculacao_quandoSolicitado() {
        when(repository.findById(1)).thenReturn(Optional.of(outroRegistroCalistenia()));

        service.editarRegistro(1, umRegistroAtividadeRequestParaCalistenia(), umUsuarioAdmin());

        verify(repository).findById(1);
        verify(repository).save(registroCalisteniaCaptor.capture());

        var registroCalistenia = registroCalisteniaCaptor.getValue();

        assertAll(
            () -> assertEquals(10, registroCalistenia.getPesoAdicional()),
            () -> assertEquals(KG, registroCalistenia.getUnidadePeso()),
            () -> assertEquals(20, registroCalistenia.getQtdRepeticoes()),
            () -> assertEquals(5, registroCalistenia.getQtdSeries()),
            () -> assertEquals("Abdominal Supra", registroCalistenia.getExercicio().getNome()),
            () -> assertEquals("Usuario Admin", registroCalistenia.getUsuario().getNome())
        );
    }

    @Test
    void excluirRegistro_deveLancarException_quandoNaoEncontrarRegistro() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        var exception = assertThrowsExactly(
            NotFoundException.class,
            () -> service.excluirRegistro(1, umUsuario())
        );
        assertEquals("Registro de calistenia não encontrado.", exception.getMessage());

        verify(repository).findById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void excluirRegistro_deveLancarException_quandoUsuarioNaoForAdminNemOMesmoUsuarioQueCriouORegistro() {
        when(repository.findById(1)).thenReturn(Optional.of(umRegistroCalistenia()));

        var exception = assertThrowsExactly(
            PermissaoException.class,
            () -> service.excluirRegistro(1, outroUsuario())
        );
        assertEquals(
            "Apenas usuários Admin ou o próprio usuário podem excluir este registro de calistenia.",
            exception.getMessage()
        );

        verify(repository).findById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void excluirRegistro_deveExcluirRegistroMusculacao_quandoSolicitado() {
        var registroCalistenia = umRegistroCalistenia();
        when(repository.findById(1)).thenReturn(Optional.of(registroCalistenia));

        assertDoesNotThrow(() -> service.excluirRegistro(1, umUsuarioAdmin()));

        verify(repository).findById(1);
        verify(repository).delete(registroCalistenia);
    }
}
