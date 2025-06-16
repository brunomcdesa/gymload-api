package br.com.gymloadapi.modulos.treino.service;

import br.com.gymloadapi.modulos.cache.config.CacheConfig;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static br.com.gymloadapi.modulos.cache.utils.CacheUtils.getCachesTreinos;
import static br.com.gymloadapi.modulos.comum.enums.EAcao.*;
import static br.com.gymloadapi.modulos.comum.enums.ESituacao.ATIVO;
import static br.com.gymloadapi.modulos.comum.enums.ESituacao.INATIVO;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.outraListaDeExercicios;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umaListaDeExercicios;
import static br.com.gymloadapi.modulos.treino.helper.TreinoHelper.*;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import({TreinoServiceTest.TestServiceConfig.class, CacheConfig.class})
class TreinoServiceTest {

    @TestConfiguration
    static class TestServiceConfig {
        @Bean
        public TreinoMapper treinoMapper() {
            return new TreinoMapperImpl();
        }

        @Bean
        public TreinoService treinoService(TreinoRepository repository, TreinoMapper treinoMapper,
                                           ExercicioService exercicioService, TreinoHistoricoService treinoHistoricoService) {
            return new TreinoService(treinoMapper, repository, exercicioService, treinoHistoricoService);
        }
    }

    @Autowired
    private TreinoService service;
    @Autowired
    private CacheManager cacheManager;
    @MockitoBean
    private TreinoRepository repository;
    @MockitoBean
    private ExercicioService exercicioService;
    @MockitoBean
    private TreinoHistoricoService historicoService;
    @Captor
    private ArgumentCaptor<Treino> captor;

    @BeforeEach
    void setUp() {
        getCachesTreinos().stream()
            .map(cacheManager::getCache)
            .filter(Objects::nonNull)
            .forEach(Cache::clear);
    }

    @Test
    void salvar_deveSalvarTreino_quandoSolicitado() {
        when(exercicioService.findByIdIn(List.of(1, 2))).thenReturn(umaListaDeExercicios());

        service.salvar(umTreinoRequest(), umUsuarioAdmin());

        verify(exercicioService).findByIdIn(List.of(1, 2));
        verify(repository).save(captor.capture());
        verify(historicoService).salvar(any(Treino.class), eq(1), eq(CADASTRO));

        var treino = captor.getValue();

        assertAll(
            () -> assertEquals("Um Treino", treino.getNome()),
            () -> assertEquals("Usuario Admin", treino.getUsuario().getNome()),
            () -> assertEquals("SUPINO RETO", treino.getExercicios().getFirst().getNome()),
            () -> assertEquals("PUXADA ALTA", treino.getExercicios().getLast().getNome())
        );
    }

    @Test
    void salvar_deveRemoverCachesTreinos_quandoSalvarTreinoNovoParaOUsuario() {
        service.listarTodosAtivosDoUsuario(1);
        service.listarTodosDoUsuario(1);

        service.salvar(umTreinoRequest(), umUsuarioAdmin());

        service.listarTodosAtivosDoUsuario(1);
        service.listarTodosDoUsuario(1);

        verify(repository, times(2)).findByUsuarioIdAndSituacaoIn(1, List.of(ATIVO));
        verify(repository, times(2)).findByUsuarioIdAndSituacaoIn(1, List.of(ATIVO, INATIVO));
        verify(exercicioService).findByIdIn(List.of(1, 2));
        verify(repository).save(any(Treino.class));
        verify(historicoService).salvar(any(Treino.class), eq(1), eq(CADASTRO));
    }

    @Test
    void listarTodosAtivosDoUsuario_deveRetornarTreinosAtivosDoUsuario_quandoSolicitado() {
        var usuario = umUsuarioAdmin();
        when(repository.findByUsuarioIdAndSituacaoIn(usuario.getId(), List.of(ATIVO)))
            .thenReturn(List.of(umTreino(ATIVO)));

        var response = service.listarTodosAtivosDoUsuario(usuario.getId());
        assertAll(
            () -> assertEquals(1, response.getFirst().id()),
            () -> assertEquals("Um Treino", response.getFirst().nome()),
            () -> assertEquals(LocalDate.of(2025, 4, 6), response.getFirst().dataCadastro()),
            () -> assertEquals(ATIVO, response.getFirst().situacao())
        );

        verify(repository).findByUsuarioIdAndSituacaoIn(usuario.getId(), List.of(ATIVO));
    }

    @Test
    void listarTodosAtivosDoUsuario_deveRetornarDadosDoCache_quandoSolicitadoVariasVezes() {
        service.listarTodosAtivosDoUsuario(1);
        service.listarTodosAtivosDoUsuario(1);
        service.listarTodosAtivosDoUsuario(1);

        verify(repository).findByUsuarioIdAndSituacaoIn(1, List.of(ATIVO));
    }

    @Test
    void listarTodosDoUsuario_deveRetornarTodosOsTreinosDoUsuario_quandoSolicitado() {
        var usuario = umUsuarioAdmin();
        when(repository.findByUsuarioIdAndSituacaoIn(usuario.getId(), List.of(ATIVO, INATIVO)))
            .thenReturn(List.of(umTreino(ATIVO), umTreino(INATIVO)));

        var response = service.listarTodosDoUsuario(usuario.getId());
        assertAll(
            () -> assertEquals(1, response.getFirst().id()),
            () -> assertEquals("Um Treino", response.getFirst().nome()),
            () -> assertEquals(LocalDate.of(2025, 4, 6), response.getFirst().dataCadastro()),
            () -> assertEquals(ATIVO, response.getFirst().situacao()),
            () -> assertEquals(INATIVO, response.getLast().situacao())
        );

        verify(repository).findByUsuarioIdAndSituacaoIn(usuario.getId(), List.of(ATIVO, INATIVO));
    }

    @Test
    void listarTodosDoUsuario_deveRetornarDadosDoCache_quandoSolicitadoVariasVezes() {
        service.listarTodosDoUsuario(1);
        service.listarTodosDoUsuario(1);
        service.listarTodosDoUsuario(1);

        verify(repository).findByUsuarioIdAndSituacaoIn(1, List.of(ATIVO, INATIVO));
    }

    @Test
    void editar_deveEditarExerciciosDoTreino_quandoNomeEListaForDiferenteDaAnterior() {
        when(repository.findCompleteById(1)).thenReturn(Optional.of(umTreino(ATIVO)));
        when(exercicioService.findByIdIn(List.of(3, 4))).thenReturn(outraListaDeExercicios());

        service.editar(1, outroTreinoRequest(), 1);

        verify(repository).findCompleteById(1);
        verify(exercicioService).findByIdIn(List.of(3, 4));
        verify(repository).save(captor.capture());

        var treino = captor.getValue();
        assertAll(
            () -> assertEquals("Outro Treino", treino.getNome()),
            () -> assertEquals(3, treino.getExercicios().getFirst().getId()),
            () -> assertEquals(4, treino.getExercicios().getLast().getId())
        );
    }

    @Test
    void editar_deveEditarExerciciosDoTreino_quandoNomeDiferenteDoAnterios() {
        when(repository.findCompleteById(1)).thenReturn(Optional.of(umTreino(ATIVO)));
        when(exercicioService.findByIdIn(List.of(1, 2))).thenReturn(umaListaDeExercicios());

        service.editar(1, maisUmTreinoRequest(), 1);

        verify(repository).findCompleteById(1);
        verify(exercicioService).findByIdIn(List.of(1, 2));
        verify(repository).save(captor.capture());
        verify(historicoService).salvar(any(Treino.class), eq(1), eq(EDICAO));

        var treino = captor.getValue();
        assertAll(
            () -> assertEquals("Outro Treino", treino.getNome()),
            () -> assertEquals(1, treino.getExercicios().getFirst().getId()),
            () -> assertEquals(2, treino.getExercicios().getLast().getId())
        );
    }

    @Test
    void editar_naoDeveEditarExerciciosDoTreino_quandoNomeEListaForIgualDaAnterior() {
        when(repository.findCompleteById(1)).thenReturn(Optional.of(umTreino(ATIVO)));

        service.editar(1, umTreinoRequest(), 1);

        verify(repository).findCompleteById(1);
        verifyNoInteractions(exercicioService);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void editar_deveLancarException_quandoNaoEncontrarTreino() {
        when(repository.findCompleteById(1)).thenReturn(Optional.empty());

        var exception = assertThrowsExactly(
            NotFoundException.class,
            () -> service.editar(1, umTreinoRequest(), 1)
        );
        assertEquals("Treino não encontrado.", exception.getMessage());

        verify(repository).findCompleteById(1);
        verifyNoInteractions(exercicioService);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void editar_deveRemoverCachesTreinos_quandoAlterarAlgumTreino() {
        when(repository.findCompleteById(1)).thenReturn(Optional.of(umTreino(ATIVO)));
        when(exercicioService.findByIdIn(List.of(1, 2))).thenReturn(umaListaDeExercicios());

        service.listarTodosAtivosDoUsuario(1);
        service.listarTodosDoUsuario(1);

        service.editar(1, maisUmTreinoRequest(), 1);

        service.listarTodosAtivosDoUsuario(1);
        service.listarTodosDoUsuario(1);

        verify(repository, times(2)).findByUsuarioIdAndSituacaoIn(1, List.of(ATIVO));
        verify(repository, times(2)).findByUsuarioIdAndSituacaoIn(1, List.of(ATIVO, INATIVO));
        verify(exercicioService).findByIdIn(List.of(1, 2));
        verify(repository).findCompleteById(1);
        verify(repository).save(any(Treino.class));
        verify(historicoService).salvar(any(Treino.class), eq(1), eq(EDICAO));
    }

    @Test
    void ativar_deveLancarException_quandoNaoEncontrTreino() {
        when(repository.findCompleteById(1)).thenReturn(Optional.empty());

        var exception = assertThrowsExactly(
            NotFoundException.class,
            () -> service.ativar(1, 1)
        );
        assertEquals("Treino não encontrado.", exception.getMessage());

        verify(repository).findCompleteById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void ativar_deveLancarException_quandoTreinoJaEstiverAtivo() {
        when(repository.findCompleteById(1)).thenReturn(Optional.of(umTreino(ATIVO)));

        var exception = assertThrowsExactly(
            ValidacaoException.class,
            () -> service.ativar(1, 1)
        );
        assertEquals("O treino já está na situacão ATIVO", exception.getMessage());

        verify(repository).findCompleteById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void ativar_deveAtivarTreino_quandoTreinoEstiverInativo() {
        when(repository.findCompleteById(1)).thenReturn(Optional.of(umTreino(INATIVO)));

        service.ativar(1, 1);

        verify(repository).findCompleteById(1);
        verify(repository).save(captor.capture());

        assertEquals(ATIVO, captor.getValue().getSituacao());
    }

    @Test
    void ativar_deveRemoverCachesTreinos_quandoAtivarUmTreinoDoUsuario() {
        when(repository.findCompleteById(1)).thenReturn(Optional.of(umTreino(INATIVO)));

        service.listarTodosAtivosDoUsuario(1);
        service.listarTodosDoUsuario(1);

        service.ativar(1, 1);

        service.listarTodosAtivosDoUsuario(1);
        service.listarTodosDoUsuario(1);

        verify(repository, times(2)).findByUsuarioIdAndSituacaoIn(1, List.of(ATIVO));
        verify(repository, times(2)).findByUsuarioIdAndSituacaoIn(1, List.of(ATIVO, INATIVO));
        verify(repository).findCompleteById(1);
        verify(repository).save(any(Treino.class));
        verify(historicoService).salvar(any(Treino.class), eq(1), eq(ATIVACAO));
    }

    @Test
    void inativar_deveLancarException_quandoNaoEncontrTreino() {
        when(repository.findCompleteById(1)).thenReturn(Optional.empty());

        var exception = assertThrowsExactly(
            NotFoundException.class,
            () -> service.inativar(1, 1)
        );
        assertEquals("Treino não encontrado.", exception.getMessage());

        verify(repository).findCompleteById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void inativar_deveLancarException_quandoTreinoJaEstiverInativo() {
        when(repository.findCompleteById(1)).thenReturn(Optional.of(umTreino(INATIVO)));

        var exception = assertThrowsExactly(
            ValidacaoException.class,
            () -> service.inativar(1, 1)
        );
        assertEquals("O treino já está na situacão INATIVO", exception.getMessage());

        verify(repository).findCompleteById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void inativar_deveInativarTreino_quandoTreinoEstiverAtivo() {
        when(repository.findCompleteById(1)).thenReturn(Optional.of(umTreino(ATIVO)));

        service.inativar(1, 1);

        verify(repository).findCompleteById(1);
        verify(repository).save(captor.capture());

        assertEquals(INATIVO, captor.getValue().getSituacao());
    }

    @Test
    void inativar_deveRemoverCachesTreinos_quandoInativarUmTreinoDoUsuario() {
        when(repository.findCompleteById(1)).thenReturn(Optional.of(umTreino(ATIVO)));

        service.listarTodosAtivosDoUsuario(1);
        service.listarTodosDoUsuario(1);

        service.inativar(1, 1);

        service.listarTodosAtivosDoUsuario(1);
        service.listarTodosDoUsuario(1);

        verify(repository, times(2)).findByUsuarioIdAndSituacaoIn(1, List.of(ATIVO));
        verify(repository, times(2)).findByUsuarioIdAndSituacaoIn(1, List.of(ATIVO, INATIVO));
        verify(repository).findCompleteById(1);
        verify(repository).save(any(Treino.class));
        verify(historicoService).salvar(any(Treino.class), eq(1), eq(INATIVACAO));
    }
}
