package br.com.gymloadapi.modulos.exercicio.service;

import br.com.gymloadapi.modulos.cache.config.CacheConfig;
import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.exercicio.mapper.ExercicioMapper;
import br.com.gymloadapi.modulos.exercicio.mapper.ExercicioMapperImpl;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.exercicio.repository.ExercicioRepository;
import br.com.gymloadapi.modulos.grupomuscular.service.GrupoMuscularService;
import com.querydsl.core.types.Predicate;
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

import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static br.com.gymloadapi.modulos.cache.utils.CacheUtils.getCachesExercicio;
import static br.com.gymloadapi.modulos.comum.enums.EAcao.CADASTRO;
import static br.com.gymloadapi.modulos.comum.enums.EAcao.EDICAO;
import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.AEROBICO;
import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.MUSCULACAO;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.*;
import static br.com.gymloadapi.modulos.grupomuscular.helper.GrupoMuscularHelper.umGrupoMuscularPeitoral;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import({ExercicioServiceTest.TestServiceConfig.class, CacheConfig.class})
class ExercicioServiceTest {

    @TestConfiguration
    static class TestServiceConfig {
        @Bean
        public ExercicioMapper exercicioMapper() {
            return new ExercicioMapperImpl();
        }

        @Bean
        public ExercicioService exercicioService(ExercicioRepository repository, ExercicioMapper exercicioMapper,
                                                 GrupoMuscularService grupoMuscularService,
                                                 ExercicioHistoricoService historicoService) {
            return new ExercicioService(repository, exercicioMapper, grupoMuscularService, historicoService);
        }
    }

    @Autowired
    private ExercicioService service;
    @Autowired
    private CacheManager cacheManager;
    @MockitoBean
    private ExercicioRepository repository;
    @MockitoBean
    private GrupoMuscularService grupoMuscularService;
    @MockitoBean
    private ExercicioHistoricoService historicoService;
    @Captor
    private ArgumentCaptor<Exercicio> exercicioCaptor;

    @BeforeEach
    void setUp() {
        getCachesExercicio().stream()
            .map(cacheManager::getCache)
            .filter(Objects::nonNull)
            .forEach(Cache::clear);
    }

    @Test
    void salvar_deveSalvarExercicioMusculacao_quandoSolicitado() {
        when(grupoMuscularService.findById(1)).thenReturn(umGrupoMuscularPeitoral());

        service.salvar(umExercicioMusculacaoRequest(), 1);

        verify(grupoMuscularService).findById(1);
        verify(repository).save(exercicioCaptor.capture());
        verify(historicoService).salvar(any(Exercicio.class), eq(1), eq(CADASTRO));

        var exercicio = exercicioCaptor.getValue();
        assertAll(
            () -> assertEquals("SUPINO RETO", exercicio.getNome()),
            () -> assertEquals("Supino Reto", exercicio.getDescricao()),
            () -> assertEquals(MUSCULACAO, exercicio.getTipoExercicio()),
            () -> assertEquals("Peitoral", exercicio.getGrupoMuscular().getNome()),
            () -> assertTrue(exercicio.getPossuiVariacao())
        );
    }

    @Test
    void salvar_deveSalvarExercicioAerobico_quandoSolicitadoComTipoExercicioAerobico() {
        service.salvar(umExercicioAerobicoRequest(), 1);

        verify(repository).save(exercicioCaptor.capture());
        verify(historicoService).salvar(any(Exercicio.class), eq(1), eq(CADASTRO));
        verifyNoInteractions(grupoMuscularService);

        var exercicio = exercicioCaptor.getValue();
        assertAll(
            () -> assertEquals("ESCADA", exercicio.getNome()),
            () -> assertEquals("Escada", exercicio.getDescricao()),
            () -> assertEquals(AEROBICO, exercicio.getTipoExercicio()),
            () -> assertNull(exercicio.getGrupoMuscular()),
            () -> assertFalse(exercicio.getPossuiVariacao())
        );
    }

    @Test
    void salvar_deveRemoverTodosOsCachesDeExercicios_quandoSalvarUmNovoExercicio() {
        service.buscarTodosSelect();
        service.buscarExerciciosPorTreino(1);
        service.buscarTodos(umExercicioFiltro());
        service.findByIdIn(List.of(1));

        service.salvar(umExercicioAerobicoRequest(), 1);

        service.buscarTodosSelect();
        service.buscarExerciciosPorTreino(1);
        service.buscarTodos(umExercicioFiltro());
        service.findByIdIn(List.of(1));

        verify(repository, times(2)).findAllComplete();
        verify(repository, times(2)).buscarExerciciosPorTreino(1);
        verify(repository, times(2)).findAllCompleteByPredicate(any(Predicate.class));
        verify(repository, times(2)).findByIdIn(List.of(1));
        verify(repository).save(any(Exercicio.class));
        verify(historicoService).salvar(any(Exercicio.class), eq(1), eq(CADASTRO));
        verifyNoInteractions(grupoMuscularService);
    }

    @Test
    void salvar_deveLancarException_quandoSolicitadoComTipoExercicioMusculacaoECamposObrigatoriosInvalidos() {
        var request = umExercicioRequestMusculacaoComCamposInvalidos("teste");

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> service.salvar(request, 1));
        assertThat(exception.getMessage())
            .contains("grupoMuscularId: é obrigatório.");

        verifyNoInteractions(grupoMuscularService, repository, historicoService);
    }

    @Test
    void salvar_deveLancarException_quandoSolicitadoComTipoExercicioAerobicoEObrigatoriosInvalidos() {
        var request = umExercicioRequestAerobicoComCamposInvalidos("teste");

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> service.salvar(request, 1));
        assertThat(exception.getMessage())
            .contains("grupoMuscularId: deve ser nulo");

        verifyNoInteractions(grupoMuscularService, repository, historicoService);
    }

    @Test
    void buscarTodos_deveRetornarTodosOsExercicios_quandoSolicitado() {
        when(repository.findAllCompleteByPredicate(any(Predicate.class))).thenReturn(umaListaDeExercicios());

        var responses = service.buscarTodos(umExercicioFiltro());

        assertAll(
            () -> assertEquals(1, responses.getFirst().id()),
            () -> assertEquals("SUPINO RETO", responses.getFirst().nome()),
            () -> assertEquals(2, responses.getLast().id()),
            () -> assertEquals("PUXADA ALTA", responses.getLast().nome())
        );

        verify(repository).findAllCompleteByPredicate(any(Predicate.class));
    }

    @Test
    void buscarTodos_deveRetornarDadosDoCache_quandoSolicitadoVariasVezesSeguidas() {
        when(repository.findAllCompleteByPredicate(any(Predicate.class))).thenReturn(emptyList());

        service.buscarTodos(umExercicioFiltro());
        service.buscarTodos(umExercicioFiltro());
        service.buscarTodos(umExercicioFiltro());

        verify(repository).findAllCompleteByPredicate(any(Predicate.class));
    }

    @Test
    void findById_deveLancarException_quandoNaoEncontrarExercicio() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        var exception = assertThrowsExactly(NotFoundException.class, () -> service.findById(1));
        assertEquals("Exercício não encontrado.", exception.getMessage());

        verify(repository).findById(1);
    }

    @Test
    void findById_deveRetornarExercicio_quandoEncontrarExercicio() {
        when(repository.findById(1)).thenReturn(Optional.of(umExercicioMusculacao(1)));

        var exercicio = assertDoesNotThrow(() -> service.findById(1));
        assertAll(
            () -> assertEquals(1, exercicio.getId()),
            () -> assertEquals("SUPINO RETO", exercicio.getNome())
        );

        verify(repository).findById(1);
    }

    @Test
    void findById_deveRetornarExercicioDoCache_quandoSolicitadoVariasVezes() {
        when(repository.findById(1)).thenReturn(Optional.of(umExercicioMusculacao(1)));

        service.findById(1);
        service.findById(1);
        service.findById(1);

        verify(repository).findById(1);
    }

    @Test
    void buscarTodosSelect_deveRetornarSelectDeExercicios_quandoSolicitado() {
        when(repository.findAllComplete()).thenReturn(umaListaDeExercicios());

        var exerciciosSelect = service.buscarTodosSelect();
        assertAll(
            () -> assertEquals(1, exerciciosSelect.getFirst().value()),
            () -> assertEquals("SUPINO RETO", exerciciosSelect.getFirst().label()),
            () -> assertEquals(2, exerciciosSelect.getLast().value()),
            () -> assertEquals("PUXADA ALTA", exerciciosSelect.getLast().label())
        );

        verify(repository).findAllComplete();
    }

    @Test
    void buscarTodosSelect_deveRetornarDadosDoCache_quandoSolicitadoVariasVezes() {
        when(repository.findAllComplete()).thenReturn(umaListaDeExercicios());

        service.buscarTodosSelect();
        service.buscarTodosSelect();
        service.buscarTodosSelect();

        verify(repository).findAllComplete();
    }

    @Test
    void findByIdIn_deveRetornarExercicios_quandoSolicitado() {
        when(repository.findByIdIn(List.of(1, 2))).thenReturn(umaListaDeExercicios());

        var exercicios = service.findByIdIn(List.of(1, 2));

        assertAll(
            () -> assertEquals(1, exercicios.getFirst().getId()),
            () -> assertEquals("SUPINO RETO", exercicios.getFirst().getNome()),
            () -> assertEquals(2, exercicios.getLast().getId()),
            () -> assertEquals("PUXADA ALTA", exercicios.getLast().getNome())
        );

        verify(repository).findByIdIn(List.of(1, 2));
    }

    @Test
    void findByIdIn_deveRetornarDadosDoCache_quandoSolicitadoVariasVezes() {
        when(repository.findByIdIn(List.of(1, 2))).thenReturn(umaListaDeExercicios());

        service.findByIdIn(List.of(1, 2));
        service.findByIdIn(List.of(1, 2));
        service.findByIdIn(List.of(1, 2));

        verify(repository).findByIdIn(List.of(1, 2));
    }

    @Test
    void buscarExerciciosPorTreino_deveRetornarExerciciosPorTreinoEUsuario_quandoSolicitado() {
        when(repository.buscarExerciciosPorTreino(1)).thenReturn(umaListaDeExercicios());

        var exercicios = service.buscarExerciciosPorTreino(1);

        assertAll(
            () -> assertEquals(1, exercicios.getFirst().id()),
            () -> assertEquals("SUPINO RETO", exercicios.getFirst().nome()),
            () -> assertEquals(2, exercicios.getLast().id()),
            () -> assertEquals("PUXADA ALTA", exercicios.getLast().nome())
        );

        verify(repository).buscarExerciciosPorTreino(1);
    }

    @Test
    void buscarExerciciosPorTreino_deveRetornarDadosDoCache_quandoSolicitadoVariasVezes() {
        when(repository.buscarExerciciosPorTreino(1)).thenReturn(umaListaDeExercicios());

        service.buscarExerciciosPorTreino(1);
        service.buscarExerciciosPorTreino(1);
        service.buscarExerciciosPorTreino(1);

        verify(repository).buscarExerciciosPorTreino(1);
    }

    @Test
    void editar_deveLancarException_quandoNaoEncontrarExercicio() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        var exception = assertThrowsExactly(NotFoundException.class,
            () -> service.editar(1, umExercicioMusculacaoRequest(), 1));
        assertEquals("Exercício não encontrado.", exception.getMessage());

        verify(repository).findById(1);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(grupoMuscularService, historicoService);
    }

    @Test
    void editar_deveLancarException_quandoTentarAlterarOTipoDeExercicio() {
        when(repository.findById(1)).thenReturn(Optional.of(umExercicioMusculacao(1)));

        var exception = assertThrowsExactly(ValidacaoException.class,
            () -> service.editar(1, umExercicioAerobicoRequest(), 1));
        assertEquals("Não é permitido alterar o tipo de exercício.", exception.getMessage());

        verify(repository).findById(1);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(grupoMuscularService, historicoService);
    }

    @Test
    void editar_deveLancarException_quandoSolicitadoComTipoExercicioMusculacaoECamposObrigatoriosInvalidos() {
        when(repository.findById(1)).thenReturn(Optional.of(umExercicioMusculacao(1)));

        var exception = assertThrowsExactly(ConstraintViolationException.class,
            () -> service.editar(1, umExercicioRequestMusculacaoComCamposInvalidos("teste"), 1));
        assertThat(exception.getMessage())
            .contains("grupoMuscularId: é obrigatório.");

        verify(repository).findById(1);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(grupoMuscularService, historicoService);
    }

    @Test
    void editar_deveLancarException_quandoSolicitadoComTipoExercicioAerobicoECamposObrigatoriosInvalidos() {
        when(repository.findById(1)).thenReturn(Optional.of(umExercicioAerobico(1)));

        var exception = assertThrowsExactly(ConstraintViolationException.class,
            () -> service.editar(1, umExercicioRequestAerobicoComCamposInvalidos("teste"), 1));
        assertThat(exception.getMessage())
            .contains("grupoMuscularId: deve ser nulo");

        verify(repository).findById(1);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(grupoMuscularService, historicoService);
    }

    @Test
    void editar_deveEditarExercicio_quandoSolicitadoComTipoExercicioMusculacaoECamposObrigatoriosValidos() {
        when(repository.findById(1)).thenReturn(Optional.of(outroExercicioMusculacao(1)));
        when(grupoMuscularService.findById(1)).thenReturn(umGrupoMuscularPeitoral());

        assertDoesNotThrow(() -> service.editar(1, umExercicioMusculacaoRequest(), 1));

        verify(repository).findById(1);
        verify(grupoMuscularService).findById(1);
        verify(repository).save(exercicioCaptor.capture());
        verify(historicoService).salvar(any(Exercicio.class), eq(1), eq(EDICAO));

        var exercicio = exercicioCaptor.getValue();
        assertAll(
            () -> assertEquals("SUPINO RETO", exercicio.getNome()),
            () -> assertEquals("Supino Reto", exercicio.getDescricao()),
            () -> assertEquals(MUSCULACAO, exercicio.getTipoExercicio()),
            () -> assertEquals("Peitoral", exercicio.getGrupoMuscular().getNome()),
            () -> assertTrue(exercicio.getPossuiVariacao())
        );
    }

    @Test
    void editar_deveEditarExercicio_quandoSolicitadoComTipoExercicioAerobicoECamposObrigatoriosValidos() {
        when(repository.findById(1)).thenReturn(Optional.of(umExercicioAerobico(1)));

        assertDoesNotThrow(() -> service.editar(1, umExercicioAerobicoRequest(), 1));

        verify(repository).findById(1);
        verify(repository).save(exercicioCaptor.capture());
        verify(historicoService).salvar(any(Exercicio.class), eq(1), eq(EDICAO));
        verifyNoInteractions(grupoMuscularService);

        var exercicio = exercicioCaptor.getValue();
        assertAll(
            () -> assertEquals("ESCADA", exercicio.getNome()),
            () -> assertEquals("Escada", exercicio.getDescricao()),
            () -> assertEquals(AEROBICO, exercicio.getTipoExercicio()),
            () -> assertNull(exercicio.getGrupoMuscular()),
            () -> assertFalse(exercicio.getPossuiVariacao())
        );
    }

    @Test
    void editar_deveRemoverTodosOsCachesDeExercicio_quandoSolicitadoEditarUmExercicio() {
        when(repository.findById(2)).thenReturn(Optional.of(umExercicioAerobico(2)));

        service.buscarTodosSelect();
        service.buscarExerciciosPorTreino(1);
        service.buscarTodos(umExercicioFiltro());
        service.findByIdIn(List.of(1));
        service.findById(2);

        service.editar(2, umExercicioAerobicoRequest(), 1);

        service.buscarTodosSelect();
        service.buscarExerciciosPorTreino(1);
        service.buscarTodos(umExercicioFiltro());
        service.findByIdIn(List.of(1));
        service.findById(2);

        verify(repository, times(2)).findAllComplete();
        verify(repository, times(2)).buscarExerciciosPorTreino(1);
        verify(repository, times(2)).findAllCompleteByPredicate(any(Predicate.class));
        verify(repository, times(2)).findByIdIn(List.of(1));
        verify(repository, times(3)).findById(2);
        verify(repository).save(exercicioCaptor.capture());
        verify(historicoService).salvar(any(Exercicio.class), eq(1), eq(EDICAO));
        verifyNoInteractions(grupoMuscularService);
    }
}
