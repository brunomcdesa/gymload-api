package br.com.gymloadapi.modulos.registroatividade.registrocardio.service;

import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.PermissaoException;
import br.com.gymloadapi.modulos.registroatividade.mapper.RegistroAtividadeMapper;
import br.com.gymloadapi.modulos.registroatividade.mapper.RegistroAtividadeMapperImpl;
import br.com.gymloadapi.modulos.registroatividade.registrocardio.model.RegistroCardio;
import br.com.gymloadapi.modulos.registroatividade.registrocardio.repository.RegistroCardioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioAerobico;
import static br.com.gymloadapi.modulos.registroatividade.helper.RegistroAtividadeHelper.umRegistroAtividadeRequestParaAerobico;
import static br.com.gymloadapi.modulos.registroatividade.registrocardio.helper.RegistroCardioHelper.umRegistroCardio;
import static br.com.gymloadapi.modulos.registroatividade.registrocardio.helper.RegistroCardioHelper.umaListaDeRegistrosCardio;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.*;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistroCardioServiceTest {

    private RegistroCardioService service;
    private final RegistroAtividadeMapper registroAtividadeMapper = new RegistroAtividadeMapperImpl();

    @Mock
    private RegistroCardioRepository repository;
    @Captor
    private ArgumentCaptor<RegistroCardio> registroCardioCaptor;

    @BeforeEach
    void setUp() {
        service = new RegistroCardioService(repository, registroAtividadeMapper);
    }

    @Test
    void salvarRegistro_deveSalvarRegistro_quandoSolicitado() {
        service.salvarRegistro(umRegistroAtividadeRequestParaAerobico(), umExercicioAerobico(1), umUsuario());

        verify(repository).save(registroCardioCaptor.capture());

        var registroCardio = registroCardioCaptor.getValue();
        assertAll(
            () -> assertEquals(20.0, registroCardio.getDistancia()),
            () -> assertEquals(1.5, registroCardio.getDuracao()),
            () -> assertEquals("Esteira", registroCardio.getExercicio().getNome()),
            () -> assertEquals("Usuario", registroCardio.getUsuario().getNome())
        );
    }

    @Test
    void buscarUltimoRegistro_deveRetornarDadosNull_quandoNaoEcontrarRegistrosParaOExercicio() {
        when(repository.findAllByExercicioIdAndUsuarioId(1, 1)).thenReturn(emptyList());

        var response = service.buscarUltimoRegistro(1, 1);
        assertAll(
            () -> assertNull(response.destaque()),
            () -> assertTrue(response.historicoRegistroAtividade().isEmpty())
        );

        verify(repository).findAllByExercicioIdAndUsuarioId(1, 1);
    }

    @Test
    void buscarUltimoRegistro_deveRetornarDadosPreenchidos_quandoEcontrarRegistrosParaOExercicio() {
        when(repository.findAllByExercicioIdAndUsuarioId(1, 1)).thenReturn(umaListaDeRegistrosCardio());

        var response = service.buscarUltimoRegistro(1, 1);
        assertAll(
            () -> assertEquals("26,60 km", response.destaque()),
            () -> assertEquals(1, response.historicoRegistroAtividade().getFirst().id()),
            () -> assertEquals("Esteira", response.historicoRegistroAtividade().getFirst().exercicioNome()),
            () -> assertEquals(LocalDate.of(2025, 4, 14), response.historicoRegistroAtividade().getFirst().dataCadastro()),
            () -> assertEquals(22.6, response.historicoRegistroAtividade().getFirst().distancia()),
            () -> assertEquals(1.33, response.historicoRegistroAtividade().getFirst().duracao()),
            () -> assertEquals("16,99 km/h", response.historicoRegistroAtividade().getFirst().velocidadeMedia())
        );

        verify(repository).findAllByExercicioIdAndUsuarioId(1, 1);
    }

    @Test
    void buscarHistoricoRegistroCompleto_deveRetornarListaVazia_quandoNaoEncontrarRegistrosParaOExercicio() {
        when(repository.findAllByExercicioIdAndUsuarioId(1, 1)).thenReturn(emptyList());

        assertTrue(service.buscarHistoricoRegistroCompleto(1, 1).isEmpty());

        verify(repository).findAllByExercicioIdAndUsuarioId(1, 1);
    }

    @Test
    void buscarHistoricoRegistroCompleto_deveRetornarDadosPreenchidos_quandoEcontrarRegistrosParaOExercicio() {
        when(repository.findAllByExercicioIdAndUsuarioId(1, 1)).thenReturn(umaListaDeRegistrosCardio());

        var response = service.buscarHistoricoRegistroCompleto(1, 1);
        assertAll(
            () -> assertEquals(1, response.getFirst().id()),
            () -> assertEquals("Esteira", response.getFirst().exercicioNome()),
            () -> assertEquals(LocalDate.of(2025, 4, 14), response.getFirst().dataCadastro()),
            () -> assertEquals(22.6, response.getFirst().distancia()),
            () -> assertEquals(1.33, response.getFirst().duracao()),
            () -> assertEquals("16,99 km/h", response.getFirst().velocidadeMedia()),
            () -> assertEquals(2, response.getLast().id()),
            () -> assertEquals("Esteira", response.getLast().exercicioNome()),
            () -> assertEquals(LocalDate.of(2025, 4, 13), response.getLast().dataCadastro()),
            () -> assertEquals(26.6, response.getLast().distancia()),
            () -> assertEquals(1.92, response.getLast().duracao()),
            () -> assertEquals("13,85 km/h", response.getLast().velocidadeMedia())
        );

        verify(repository).findAllByExercicioIdAndUsuarioId(1, 1);
    }

    @Test
    void editarRegistro_deveLancarException_quandoNaoEncontrarRegistro() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        var exception = assertThrowsExactly(
            NotFoundException.class,
            () -> service.editarRegistro(1, umRegistroAtividadeRequestParaAerobico(), umUsuarioAdmin())
        );
        assertEquals("Registro de Cardio não encontrado.", exception.getMessage());

        verify(repository).findById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void editarRegistro_deveLancarException_quandoUsuarioNaoForAdminNemOMesmoUsuarioQueCriouORegistro() {
        when(repository.findById(1)).thenReturn(Optional.of(umRegistroCardio()));

        var exception = assertThrowsExactly(
            PermissaoException.class,
            () -> service.editarRegistro(1, umRegistroAtividadeRequestParaAerobico(), outroUsuario())
        );
        assertEquals(
            "Apenas usuários Admin ou o próprio usuário podem alterar as informações deste registro de cardio.",
            exception.getMessage()
        );

        verify(repository).findById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void editarRegistro_deveEditarRegistroCargas_quandoSolicitado() {
        when(repository.findById(1)).thenReturn(Optional.of(umRegistroCardio()));

        service.editarRegistro(1, umRegistroAtividadeRequestParaAerobico(), umUsuarioAdmin());

        verify(repository).findById(1);
        verify(repository).save(registroCardioCaptor.capture());

        var registroCardio = registroCardioCaptor.getValue();
        assertAll(
            () -> assertEquals(20.0, registroCardio.getDistancia()),
            () -> assertEquals(1.5, registroCardio.getDuracao()),
            () -> assertEquals("Esteira", registroCardio.getExercicio().getNome()),
            () -> assertEquals("Usuario", registroCardio.getUsuario().getNome())
        );
    }
}
