package br.com.gymloadapi.modulos.exercicio.service;

import br.com.gymloadapi.modulos.exercicio.mapper.ExercicioMapper;
import br.com.gymloadapi.modulos.exercicio.mapper.ExercicioMapperImpl;
import br.com.gymloadapi.modulos.exercicio.model.ExercicioVariacao;
import br.com.gymloadapi.modulos.exercicio.repository.ExercicioVariacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento.BARRA;
import static br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento.HALTER;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.*;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        service.salvar(umExercicioVariacaoRequest(), 1);

        verify(exercicioService).findById(1);
        verify(repository).save(exercicioVariacaoCaptor.capture());

        var variacao = exercicioVariacaoCaptor.getValue();
        assertAll(
            () -> assertEquals("SUPINO RETO - Halter", variacao.getNome()),
            () -> assertEquals(HALTER, variacao.getTipoEquipamento()),
            () -> assertEquals(1, variacao.getExercicio().getId()),
            () -> assertEquals(1, variacao.getUsuarioCadastroId())
        );
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
