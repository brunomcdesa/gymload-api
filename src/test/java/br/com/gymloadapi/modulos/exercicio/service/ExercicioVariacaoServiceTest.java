package br.com.gymloadapi.modulos.exercicio.service;

import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.exercicio.mapper.ExercicioMapper;
import br.com.gymloadapi.modulos.exercicio.mapper.ExercicioMapperImpl;
import br.com.gymloadapi.modulos.exercicio.model.ExercicioVariacao;
import br.com.gymloadapi.modulos.exercicio.repository.ExercicioVariacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintViolationException;

import static br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento.BARRA;
import static br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento.HALTER;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.*;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExercicioVariacaoServiceTest {

    private ExercicioVariacaoService service;
    private final ExercicioMapper exercicioMapper = new ExercicioMapperImpl();

    @Mock
    private ExercicioVariacaoRepository repository;
    @Mock
    private ExercicioService exercicioService;
    @Captor
    private ArgumentCaptor<ExercicioVariacao> exercicioVariacaoCaptor;

    @BeforeEach
    void setUp() {
        service = new ExercicioVariacaoService(exercicioMapper, exercicioService, repository);
    }

    @Test
    void salvar_deveSalvarNovaVariacao_quandoSolicitado() {
        when(exercicioService.findById(1)).thenReturn(umExercicioMusculacao(1));
        when(repository.existsByTipoEquipamentoAndExercicio_Id(HALTER, 1)).thenReturn(false);

        service.salvar(umExercicioVariacaoComTipoEquipaentoRequest(), 1);

        verify(exercicioService).findById(1);
        verify(repository).existsByTipoEquipamentoAndExercicio_Id(HALTER, 1);
        verify(repository).save(exercicioVariacaoCaptor.capture());
        verifyNoMoreInteractions(repository);

        var variacao = exercicioVariacaoCaptor.getValue();
        assertAll(
            () -> assertEquals("SUPINO RETO - Halter", variacao.getNome()),
            () -> assertEquals(HALTER, variacao.getTipoEquipamento()),
            () -> assertEquals(1, variacao.getExercicio().getId()),
            () -> assertEquals(1, variacao.getUsuarioCadastroId())
        );
    }

    @Test
    void salvar_deveSalvarNovaVariacao_quandoSolicitadoParaExercicioCalistenia() {
        var exercicio = umExercicioCalistenia(1);
        exercicio.setPossuiVariacao(true);

        when(exercicioService.findById(1)).thenReturn(exercicio);
        when(repository.existsByNomeIgnoreCase("Abdominal Supra")).thenReturn(false);

        service.salvar(umExercicioVariacaoSemTipoEquipaentoRequest(), 1);

        verify(exercicioService).findById(1);
        verify(repository).existsByNomeIgnoreCase("Abdominal Supra");
        verify(repository).save(exercicioVariacaoCaptor.capture());
        verifyNoMoreInteractions(repository);

        var variacao = exercicioVariacaoCaptor.getValue();
        assertAll(
            () -> assertEquals("Abdominal Supra", variacao.getNome()),
            () -> assertNull(variacao.getTipoEquipamento()),
            () -> assertEquals(1, variacao.getExercicio().getId()),
            () -> assertEquals(1, variacao.getUsuarioCadastroId())
        );
    }

    @Test
    void salvar_deveLancarException_quandoAplicarValidacoesEmGruopoParaAerobico() {
        var request = umExercicioVariacaoRequest(1, "teste", HALTER);

        when(exercicioService.findById(1)).thenReturn(umExercicioAerobico(1));

        var exception = assertThrowsExactly(
            ConstraintViolationException.class,
            () -> service.salvar(request, 1)
        );
        assertThat(exception.getMessage())
            .contains("tipoEquipamento: deve ser nulo",
                "nome: deve ser nulo");

        verify(exercicioService).findById(1);
        verifyNoInteractions(repository);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void salvar_deveLancarException_quandoAplicarValidacoesEmGruopoParaCalistenia(String nomeInvalido) {
        var request = umExercicioVariacaoRequest(1, nomeInvalido, HALTER);

        when(exercicioService.findById(1)).thenReturn(umExercicioCalistenia(1));

        var exception = assertThrowsExactly(
            ConstraintViolationException.class,
            () -> service.salvar(request, 1)
        );
        assertThat(exception.getMessage())
            .contains("tipoEquipamento: deve ser nulo",
                "nome: é obrigatório.");

        verify(exercicioService).findById(1);
        verifyNoInteractions(repository);
    }

    @Test
    void salvar_deveLancarException_quandoAplicarValidacoesEmGruopoParaMusculacao() {
        var request = umExercicioVariacaoRequest(1, "teste", null);

        when(exercicioService.findById(1)).thenReturn(umExercicioMusculacao(1));

        var exception = assertThrowsExactly(
            ConstraintViolationException.class,
            () -> service.salvar(request, 1)
        );
        assertThat(exception.getMessage())
            .contains("tipoEquipamento: é obrigatório.",
                "nome: deve ser nulo");

        verify(exercicioService).findById(1);
        verifyNoInteractions(repository);
    }

    @Test
    void salvar_deveLancarException_quandoExercicioNaoForPermitidoParaTerVariacoes() {
        var request = umExercicioVariacaoRequest(1, "teste", null);

        when(exercicioService.findById(1)).thenReturn(umExercicioCalistenia(1));

        var exception = assertThrowsExactly(
            ValidacaoException.class,
            () -> service.salvar(request, 1)
        );
        assertEquals("Este exercício não está permitido para ter variações.", exception.getMessage());

        verify(exercicioService).findById(1);
        verifyNoInteractions(repository);
    }

    @Test
    void salvar_deveLancarException_quandoPossuirVariacaoComMesmoTipoExercicio() {
        var request = umExercicioVariacaoComTipoEquipaentoRequest();

        when(exercicioService.findById(1)).thenReturn(umExercicioMusculacao(1));
        when(repository.existsByTipoEquipamentoAndExercicio_Id(HALTER, 1)).thenReturn(true);

        var exception = assertThrowsExactly(
            ValidacaoException.class,
            () -> service.salvar(request, 1)
        );
        assertEquals("Já existe uma variação igual para este exercício.", exception.getMessage());

        verify(exercicioService).findById(1);
        verify(repository).existsByTipoEquipamentoAndExercicio_Id(HALTER, 1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void salvar_deveLancarException_quandoPossuirVariacaoComMesmoNome() {
        var exercicio = umExercicioCalistenia(1);
        exercicio.setPossuiVariacao(true);

        when(exercicioService.findById(1)).thenReturn(exercicio);
        when(repository.existsByNomeIgnoreCase("Abdominal Supra")).thenReturn(true);

        var exception = assertThrowsExactly(
            ValidacaoException.class,
            () -> service.salvar(umExercicioVariacaoSemTipoEquipaentoRequest(), 1)
        );
        assertEquals("Já existe uma variação igual para este exercício.", exception.getMessage());

        verify(exercicioService).findById(1);
        verify(repository).existsByNomeIgnoreCase("Abdominal Supra");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void buscarVariacoesDoExercicio_deveRetornarListaVazia_quandoNaoEncontrarVariacoesDoExercicio() {
        when(repository.findAllByExercicio_Id(1)).thenReturn(emptyList());

        assertTrue(service.buscarVariacoesDoExercicio(1).isEmpty());

        verify(repository).findAllByExercicio_Id(1);
    }

    @Test
    void buscarVariacoesDoExercicio_deveRetornarListaDeVariacoes_quandoEncontrarVariacoesDoExercicio() {
        when(repository.findAllByExercicio_Id(1)).thenReturn(umaListaExercicioVariacao());

        var variacoes = service.buscarVariacoesDoExercicio(1);
        assertAll(
            () -> assertEquals(1, variacoes.getFirst().id()),
            () -> assertEquals("SUPINO RETO - Halter", variacoes.getFirst().nome()),
            () -> assertEquals(HALTER, variacoes.getFirst().tipoEquipamento()),
            () -> assertEquals(2, variacoes.getLast().id()),
            () -> assertEquals("SUPINO RETO - Barra", variacoes.getLast().nome()),
            () -> assertEquals(BARRA, variacoes.getLast().tipoEquipamento())
        );

        verify(repository).findAllByExercicio_Id(1);
    }
}
