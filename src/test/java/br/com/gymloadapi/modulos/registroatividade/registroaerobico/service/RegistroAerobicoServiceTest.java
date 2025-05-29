package br.com.gymloadapi.modulos.registroatividade.registroaerobico.service;

import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.PermissaoException;
import br.com.gymloadapi.modulos.registroatividade.mapper.RegistroAtividadeMapper;
import br.com.gymloadapi.modulos.registroatividade.mapper.RegistroAtividadeMapperImpl;
import br.com.gymloadapi.modulos.registroatividade.registroaerobico.model.RegistroAerobico;
import br.com.gymloadapi.modulos.registroatividade.registroaerobico.repository.RegistroAerobicoRepository;
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
import static br.com.gymloadapi.modulos.registroatividade.registroaerobico.helper.RegistroMusculacaoHelper.umRegistroAerobico;
import static br.com.gymloadapi.modulos.registroatividade.registroaerobico.helper.RegistroMusculacaoHelper.umaListaDeRegistrosAerobicos;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.*;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistroAerobicoServiceTest {

    private RegistroAaerobicoService service;
    private final RegistroAtividadeMapper registroAtividadeMapper = new RegistroAtividadeMapperImpl();

    @Mock
    private RegistroAerobicoRepository repository;
    @Captor
    private ArgumentCaptor<RegistroAerobico> registroAerobicoCaptor;

    @BeforeEach
    void setUp() {
        service = new RegistroAaerobicoService(repository, registroAtividadeMapper);
    }

    @Test
    void salvarRegistro_deveSalvarRegistro_quandoSolicitado() {
        service.salvarRegistro(umRegistroAtividadeRequestParaAerobico(), umExercicioAerobico(1), umUsuario());

        verify(repository).save(registroAerobicoCaptor.capture());

        var registroAerobico = registroAerobicoCaptor.getValue();
        assertAll(
            () -> assertEquals(20.0, registroAerobico.getDistancia()),
            () -> assertEquals(1.5, registroAerobico.getDuracao()),
            () -> assertEquals("Esteira", registroAerobico.getExercicio().getNome()),
            () -> assertEquals("Usuario", registroAerobico.getUsuario().getNome())
        );
    }

    @Test
    void buscarDestaque_deveRetornarDadosNull_quandoNaoEcontrarRegistrosParaOExercicio() {
        when(repository.findAllByExercicioIdAndUsuarioId(1, 1)).thenReturn(emptyList());

        var response = service.buscarDestaque(1, 1);
        assertAll(
            () -> assertEquals(1, response.exercicioId()),
            () -> assertEquals("-", response.destaque()),
            () -> assertNull(response.ultimoPeso()),
            () -> assertEquals("-", response.ultimaDistancia())
        );

        verify(repository).findAllByExercicioIdAndUsuarioId(1, 1);
    }

    @Test
    void buscarDestaque_deveRetornarDadosPreenchidos_quandoEcontrarRegistrosParaOExercicio() {
        when(repository.findAllByExercicioIdAndUsuarioId(1, 1)).thenReturn(umaListaDeRegistrosAerobicos());

        var response = service.buscarDestaque(1, 1);
        assertAll(
            () -> assertEquals(1, response.exercicioId()),
            () -> assertEquals("26.6 km", response.destaque()),
            () -> assertNull(response.ultimoPeso()),
            () -> assertEquals("26.6 km", response.ultimaDistancia())
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
        when(repository.findAllByExercicioIdAndUsuarioId(1, 1)).thenReturn(umaListaDeRegistrosAerobicos());

        var response = service.buscarHistoricoRegistroCompleto(1, 1);
        assertAll(
            () -> assertEquals(1, response.getFirst().id()),
            () -> assertEquals("Esteira", response.getFirst().exercicioNome()),
            () -> assertEquals(LocalDate.of(2025, 4, 14), response.getFirst().dataCadastro()),
            () -> assertEquals(22.6, response.getFirst().distancia()),
            () -> assertEquals(1.33, response.getFirst().duracao()),
            () -> assertEquals("16.99 km/h", response.getFirst().velocidadeMedia()),
            () -> assertEquals(2, response.getLast().id()),
            () -> assertEquals("Esteira", response.getLast().exercicioNome()),
            () -> assertEquals(LocalDate.of(2025, 4, 13), response.getLast().dataCadastro()),
            () -> assertEquals(26.6, response.getLast().distancia()),
            () -> assertEquals(1.92, response.getLast().duracao()),
            () -> assertEquals("13.85 km/h", response.getLast().velocidadeMedia())
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
        assertEquals("Registro de aeróbico não encontrado.", exception.getMessage());

        verify(repository).findById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void editarRegistro_deveLancarException_quandoUsuarioNaoForAdminNemOMesmoUsuarioQueCriouORegistro() {
        when(repository.findById(1)).thenReturn(Optional.of(umRegistroAerobico()));

        var exception = assertThrowsExactly(
            PermissaoException.class,
            () -> service.editarRegistro(1, umRegistroAtividadeRequestParaAerobico(), outroUsuario())
        );
        assertEquals(
            "Apenas usuários Admin ou o próprio usuário podem alterar as informações deste registro de aeróbico.",
            exception.getMessage()
        );

        verify(repository).findById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void editarRegistro_deveEditarRegistroAerobico_quandoSolicitado() {
        when(repository.findById(1)).thenReturn(Optional.of(umRegistroAerobico()));

        service.editarRegistro(1, umRegistroAtividadeRequestParaAerobico(), umUsuarioAdmin());

        verify(repository).findById(1);
        verify(repository).save(registroAerobicoCaptor.capture());

        var registroAerobico = registroAerobicoCaptor.getValue();
        assertAll(
            () -> assertEquals(20.0, registroAerobico.getDistancia()),
            () -> assertEquals(1.5, registroAerobico.getDuracao()),
            () -> assertEquals("Esteira", registroAerobico.getExercicio().getNome()),
            () -> assertEquals("Usuario", registroAerobico.getUsuario().getNome())
        );
    }

    @Test
    void excluirRegistro_deveLancarException_quandoNaoEncontrarRegistro() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        var exception = assertThrowsExactly(
            NotFoundException.class,
            () -> service.excluirRegistro(1, umUsuario())
        );
        assertEquals("Registro de aeróbico não encontrado.", exception.getMessage());

        verify(repository).findById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void excluirRegistro_deveLancarException_quandoUsuarioNaoForAdminNemOMesmoUsuarioQueCriouORegistro() {
        when(repository.findById(1)).thenReturn(Optional.of(umRegistroAerobico()));

        var exception = assertThrowsExactly(
            PermissaoException.class,
            () -> service.excluirRegistro(1, outroUsuario())
        );
        assertEquals(
            "Apenas usuários Admin ou o próprio usuário podem excluir este registro de aeróbico.",
            exception.getMessage()
        );

        verify(repository).findById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void excluirRegistro_deveExcluirRegistroAerobico_quandoSolicitado() {
        var registroAerobico = umRegistroAerobico();
        when(repository.findById(1)).thenReturn(Optional.of(registroAerobico));

        assertDoesNotThrow(() -> service.excluirRegistro(1, umUsuarioAdmin()));

        verify(repository).findById(1);
        verify(repository).delete(registroAerobico);
    }
}
