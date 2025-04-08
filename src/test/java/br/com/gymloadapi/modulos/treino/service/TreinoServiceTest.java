package br.com.gymloadapi.modulos.treino.service;

import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.treino.mapper.TreinoMapper;
import br.com.gymloadapi.modulos.treino.mapper.TreinoMapperImpl;
import br.com.gymloadapi.modulos.treino.model.Treino;
import br.com.gymloadapi.modulos.treino.repository.TreinoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static br.com.gymloadapi.modulos.comum.enums.ESituacao.ATIVO;
import static br.com.gymloadapi.modulos.comum.enums.ESituacao.INATIVO;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.outraListaDeExercicios;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umaListaDeExercicios;
import static br.com.gymloadapi.modulos.treino.helper.TreinoHelper.*;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TreinoServiceTest {

    private TreinoService service;
    private final TreinoMapper mapper = new TreinoMapperImpl();

    @Mock
    private TreinoRepository repository;
    @Mock
    private ExercicioService exercicioService;
    @Captor
    private ArgumentCaptor<Treino> captor;

    @BeforeEach
    void setUp() {
        service = new TreinoService(mapper, repository, exercicioService);
    }

    @Test
    void salvar_deveSalvarTreino_quandoSolicitado() {
        when(exercicioService.findByIdIn(List.of(1, 2))).thenReturn(umaListaDeExercicios());

        service.salvar(umTreinoRequest(), umUsuarioAdmin());

        verify(exercicioService).findByIdIn(List.of(1, 2));
        verify(repository).save(captor.capture());

        var treino = captor.getValue();

        assertAll(
            () -> assertEquals("Um Treino", treino.getNome()),
            () -> assertEquals("Usuario Admin", treino.getUsuario().getNome()),
            () -> assertEquals("SUPINO RETO", treino.getExercicios().getFirst().getNome()),
            () -> assertEquals("PUXADA ALTA", treino.getExercicios().getLast().getNome())
        );
    }

    @Test
    void listarTodosDoUsuario_deveRetornarTreinosDoUsuario_quandoSolicitado() {
        var usuario = umUsuarioAdmin();
        when(repository.findByUsuarioId(usuario.getId())).thenReturn(List.of(umTreino(ATIVO)));

        var response = service.listarTodosDoUsuario(usuario.getId());
        assertAll(
            () -> assertEquals(1, response.getFirst().id()),
            () -> assertEquals("Um Treino", response.getFirst().nome()),
            () -> assertEquals(LocalDate.of(2025, 4, 6), response.getFirst().dataCadastro())
        );

        verify(repository).findByUsuarioId(usuario.getId());
    }

    @Test
    void editar_deveEditarExerciciosDoTreino_quandoForListaDiferenteDaAnterior() {
        when(repository.findCompleteById(1)).thenReturn(Optional.of(umTreino(ATIVO)));
        when(exercicioService.findByIdIn(List.of(3, 4))).thenReturn(outraListaDeExercicios());

        service.editar(1, outroTreinoRequest());

        verify(repository).findCompleteById(1);
        verify(exercicioService).findByIdIn(List.of(3, 4));
        verify(repository).save(captor.capture());

        var treino = captor.getValue();
        assertAll(
            () -> assertEquals(3, treino.getExercicios().getFirst().getId()),
            () -> assertEquals(4, treino.getExercicios().getLast().getId())
        );
    }

    @Test
    void editar_naoDeveEditarExerciciosDoTreino_quandoForListaIgualDaAnterior() {
        when(repository.findCompleteById(1)).thenReturn(Optional.of(umTreino(ATIVO)));

        service.editar(1, umTreinoRequest());

        verify(repository).findCompleteById(1);
        verifyNoInteractions(exercicioService);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void editar_deveLancarException_quandoNaoEncontrarTreino() {
        when(repository.findCompleteById(1)).thenReturn(Optional.empty());

        var exception = assertThrowsExactly(NotFoundException.class, () -> service.editar(1, umTreinoRequest()));
        assertEquals("Treino não encontrado.", exception.getMessage());

        verify(repository).findCompleteById(1);
        verifyNoInteractions(exercicioService);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void ativar_deveLancarException_quandoNaoEncontrTreino() {
        when(repository.findCompleteById(1)).thenReturn(Optional.empty());

        var exception = assertThrowsExactly(NotFoundException.class, () -> service.ativar(1));
        assertEquals("Treino não encontrado.", exception.getMessage());

        verify(repository).findCompleteById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void ativar_deveLancarException_quandoTreinoJaEstiverAtivo() {
        var treino = umTreino(ATIVO);
        when(repository.findCompleteById(1)).thenReturn(Optional.of(treino));

        var exception = assertThrowsExactly(ValidacaoException.class, () -> service.ativar(1));
        assertEquals("O treino já está na situacão ATIVO", exception.getMessage());

        verify(repository).findCompleteById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void ativar_deveAtivarTreino_quandoTreinoEstiverInativo() {
        var treino = umTreino(INATIVO);
        when(repository.findCompleteById(1)).thenReturn(Optional.of(treino));

        service.ativar(1);

        verify(repository).findCompleteById(1);
        verify(repository).save(captor.capture());

        assertEquals(ATIVO, captor.getValue().getSituacao());
    }

    @Test
    void inativar_deveLancarException_quandoNaoEncontrTreino() {
        when(repository.findCompleteById(1)).thenReturn(Optional.empty());

        var exception = assertThrowsExactly(NotFoundException.class, () -> service.inativar(1));
        assertEquals("Treino não encontrado.", exception.getMessage());

        verify(repository).findCompleteById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void inativar_deveLancarException_quandoTreinoJaEstiverInativo() {
        var treino = umTreino(INATIVO);
        when(repository.findCompleteById(1)).thenReturn(Optional.of(treino));

        var exception = assertThrowsExactly(ValidacaoException.class, () -> service.inativar(1));
        assertEquals("O treino já está na situacão INATIVO", exception.getMessage());

        verify(repository).findCompleteById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void inativar_deveInativarTreino_quandoTreinoEstiverAtivo() {
        var treino = umTreino(ATIVO);
        when(repository.findCompleteById(1)).thenReturn(Optional.of(treino));

        service.inativar(1);

        verify(repository).findCompleteById(1);
        verify(repository).save(captor.capture());

        assertEquals(INATIVO, captor.getValue().getSituacao());
    }

}
