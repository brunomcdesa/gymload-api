package br.com.gymloadapi.modulos.exercicio.service;

import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento.HALTER;
import static br.com.gymloadapi.modulos.comum.enums.ETipoPegada.PRONADA;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.*;
import static br.com.gymloadapi.modulos.grupomuscular.helper.GrupoMuscularHelper.umGrupoMuscularPeitoral;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExercicioServiceTest {

    private ExercicioService service;
    private final ExercicioMapper mapper = new ExercicioMapperImpl();

    @Mock
    private ExercicioRepository repository;
    @Mock
    private GrupoMuscularService grupoMuscularService;
    @Captor
    private ArgumentCaptor<Exercicio> exercicioCaptor;

    @BeforeEach
    void setUp() {
        service = new ExercicioService(repository, mapper, grupoMuscularService);
    }

    @Test
    void salvar_deveSalvarExercicio_quandoSolicitado() {
        var request = umExercicioMusculacaoRequest();
        var grupoMuscular = umGrupoMuscularPeitoral();

        when(grupoMuscularService.findById(1)).thenReturn(grupoMuscular);

        service.salvar(request);

        verify(grupoMuscularService).findById(1);
        verify(repository).save(exercicioCaptor.capture());

        var exercicio = exercicioCaptor.getValue();
        assertAll(
            () -> assertEquals("SUPINO RETO", exercicio.getNome()),
            () -> assertEquals("Supino Reto", exercicio.getDescricao()),
            () -> assertEquals(HALTER, exercicio.getTipoEquipamento()),
            () -> assertEquals(PRONADA, exercicio.getTipoPegada()),
            () -> assertEquals("Peitoral", exercicio.getGrupoMuscular().getNome())
        );
    }

    @Test
    void buscarTodos_deveRetornarTodosOsExercicios_quandoSolicitado() {
        when(repository.findAllCompleteByPredicate(any(Predicate.class))).thenReturn(umaListaDeExercicios());

        var responses = service.buscarTodos(umExercicioFiltroVazio());

        assertAll(
            () -> assertEquals(1, responses.getFirst().id()),
            () -> assertEquals("SUPINO RETO", responses.getFirst().nome()),
            () -> assertEquals(2, responses.getLast().id()),
            () -> assertEquals("PUXADA ALTA", responses.getLast().nome())
        );

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
    void buscarTodosSelect_deveRetornarSelectDeExercicios_quandoSolicitado() {
        when(repository.findAllComplete()).thenReturn(umaListaDeExercicios());

        var exerciciosSelect = service.buscarTodosSelect();
        assertAll(
            () -> assertEquals(1, exerciciosSelect.getFirst().value()),
            () -> assertEquals("SUPINO RETO (HALTER)", exerciciosSelect.getFirst().label()),
            () -> assertEquals(2, exerciciosSelect.getLast().value()),
            () -> assertEquals("PUXADA ALTA (MAQUINA)", exerciciosSelect.getLast().label())
        );

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
    void buscarExerciciosPorTreino_deveRetornarExerciciosPorTreinoEUsuario_quandoSolicitado() {
        when(repository.buscarExerciciosPorTreino(1))
            .thenReturn(umaListaDeExercicios());

        var exercicios = service.buscarExerciciosPorTreino(1);

        assertAll(
            () -> assertEquals(1, exercicios.getFirst().id()),
            () -> assertEquals("SUPINO RETO", exercicios.getFirst().nome()),
            () -> assertEquals(2, exercicios.getLast().id()),
            () -> assertEquals("PUXADA ALTA", exercicios.getLast().nome())
        );

        verify(repository).buscarExerciciosPorTreino(1);
    }
}
