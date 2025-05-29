package br.com.gymloadapi.modulos.registroatividade.service;

import br.com.gymloadapi.modulos.comum.service.LocatorService;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.registroatividade.registroaerobico.service.RegistroAaerobicoService;
import br.com.gymloadapi.modulos.registroatividade.registromusculacao.service.RegistroMusculacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento.HALTER;
import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.AEROBICO;
import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.MUSCULACAO;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.*;
import static br.com.gymloadapi.modulos.registroatividade.helper.RegistroAtividadeHelper.*;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuario;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistroAtividadeServiceTest {

    @InjectMocks
    private RegistroAtividadeService service;
    @Mock
    private LocatorService locatorService;
    @Mock
    private ExercicioService exercicioService;
    @Mock
    private RegistroMusculacaoService registroMusculacaoService;
    @Mock
    private RegistroAaerobicoService registroAaerobicoService;

    @BeforeEach
    void setUp() {
        lenient().when(locatorService.getRegistroAtividadeService(MUSCULACAO)).thenReturn(registroMusculacaoService);
        lenient().when(locatorService.getRegistroAtividadeService(AEROBICO)).thenReturn(registroAaerobicoService);
    }

    @Test
    void salvar_deveChamarMetodoSalvarDoRegistroMusculacaoService_quandoSolicitadoComRequestDeMusculacao() {
        var request = umRegistroAtividadeRequestParaMusculacao();
        var usuario = umUsuario();
        var exercicio = umExercicioMusculacao(1);

        when(exercicioService.findById(1)).thenReturn(exercicio);

        service.salvar(request, usuario);

        verify(exercicioService).findById(1);
        verify(locatorService).getRegistroAtividadeService(MUSCULACAO);
        verify(registroMusculacaoService).salvarRegistro(request, exercicio, usuario);
        verifyNoInteractions(registroAaerobicoService);
    }

    @Test
    void salvar_deveChamarMetodoSalvarDoRegistroAerobicoService_quandoSolicitadoComRequestDeAerobico() {
        var request = umRegistroAtividadeRequestParaAerobico();
        var usuario = umUsuario();
        var exercicio = umExercicioAerobico(3);

        when(exercicioService.findById(3)).thenReturn(exercicio);

        service.salvar(request, usuario);

        verify(exercicioService).findById(3);
        verify(locatorService).getRegistroAtividadeService(AEROBICO);
        verify(registroAaerobicoService).salvarRegistro(request, exercicio, usuario);
        verifyNoInteractions(registroMusculacaoService);
    }

    @Test
    void buscarDestaques_deveRetornarListaVazia_quandoNaoEncontrarNenhumExercicio() {
        when(exercicioService.findByIdIn(List.of(1, 2))).thenReturn(Collections.emptyList());

        assertTrue(service.buscarDestaques(umRegistroAtividadeFiltros(), 1).isEmpty());

        verify(exercicioService).findByIdIn(List.of(1, 2));
        verifyNoInteractions(locatorService, registroMusculacaoService, registroAaerobicoService);
    }

    @Test
    void buscarDestaques_deveRetornarDestaquesDosExercicios_quandoEncontrarExercicios() {
        when(exercicioService.findByIdIn(List.of(1, 2))).thenReturn(maisUmaListaDeExercicios());
        when(registroMusculacaoService.buscarDestaque(1, 1)).thenReturn(umRegistroAtividadeResponseComDadosDeMusculacao());
        when(registroAaerobicoService.buscarDestaque(2, 1)).thenReturn(umRegistroAtividadeResponseComDadosDeAerobico());

        var responses = service.buscarDestaques(umRegistroAtividadeFiltros(), 1);
        assertAll(
            () -> assertEquals(1, responses.getFirst().exercicioId()),
            () -> assertEquals("22.5 (KG)", responses.getFirst().destaque()),
            () -> assertEquals("20.0 (KG)", responses.getFirst().ultimoPeso()),
            () -> assertNull(responses.getFirst().ultimaDistancia()),
            () -> assertEquals(2, responses.getLast().exercicioId()),
            () -> assertEquals("22.5 KM", responses.getLast().destaque()),
            () -> assertNull(responses.getLast().ultimoPeso()),
            () -> assertEquals("11,25 KM", responses.getLast().ultimaDistancia())
        );

        verify(exercicioService).findByIdIn(List.of(1, 2));
        verify(locatorService).getRegistroAtividadeService(MUSCULACAO);
        verify(locatorService).getRegistroAtividadeService(AEROBICO);
        verify(registroMusculacaoService).buscarDestaque(1, 1);
        verify(registroAaerobicoService).buscarDestaque(2, 1);
    }

    @Test
    @SuppressWarnings("LineLength")
    void buscarRegistroAtividadeCompleto_deveChamarMetodoBuscarRegistroAtividadeCompletoDoRegistroMusculacaoService_quandoSolicitadoComExercicioDeMusculacao() {
        var exercicio = umExercicioMusculacao(1);

        when(exercicioService.findById(1)).thenReturn(exercicio);
        when(registroMusculacaoService.buscarHistoricoRegistroCompleto(1, 2))
            .thenReturn(List.of(umHistoricoRegistroAtividadeResponseDeMusculacao()));

        var response = service.buscarRegistroAtividadeCompleto(1, 2);
        assertAll(
            () -> assertEquals(1, response.getFirst().id()),
            () -> assertEquals("SUPINO RETO", response.getFirst().exercicioNome()),
            () -> assertEquals("22.5 (KG)", response.getFirst().carga()),
            () -> assertEquals(HALTER, response.getFirst().tipoExercicio()),
            () -> assertEquals("Peitoral", response.getFirst().grupoMuscularNome()),
            () -> assertEquals(12, response.getFirst().qtdRepeticoes()),
            () -> assertEquals(4, response.getFirst().qtdSeries()),
            () -> assertEquals(LocalDate.of(2025, 4, 4), response.getFirst().dataCadastro())
        );

        verify(exercicioService).findById(1);
        verify(locatorService).getRegistroAtividadeService(MUSCULACAO);
        verify(registroMusculacaoService).buscarHistoricoRegistroCompleto(1, 2);
        verifyNoInteractions(registroAaerobicoService);
    }

    @Test
    @SuppressWarnings("LineLength")
    void buscarRegistroAtividadeCompleto_deveChamarMetodoBuscarRegistroAtividadeCompletoDoRegistroAerobicoService_quandoSolicitadoComRequestDeAerobico() {
        var exercicio = umExercicioAerobico(3);

        when(exercicioService.findById(3)).thenReturn(exercicio);
        when(registroAaerobicoService.buscarHistoricoRegistroCompleto(3, 2))
            .thenReturn(List.of(umHistoricoRegistroAtividadeResponseDeAerobico()));

        var response = service.buscarRegistroAtividadeCompleto(3, 2);
        assertAll(
            () -> assertEquals(2, response.getFirst().id()),
            () -> assertEquals("ESTEIRA", response.getFirst().exercicioNome()),
            () -> assertEquals(LocalDate.of(2025, 4, 4), response.getFirst().dataCadastro()),
            () -> assertEquals(22.5, response.getFirst().distancia()),
            () -> assertEquals(2.0, response.getFirst().duracao())
        );

        verify(exercicioService).findById(3);
        verify(locatorService).getRegistroAtividadeService(AEROBICO);
        verify(registroAaerobicoService).buscarHistoricoRegistroCompleto(3, 2);
        verifyNoInteractions(registroMusculacaoService);
    }

    @Test
    void editar_deveChamarMetodoEditarDoRegistroMusculacaoService_quandoSolicitadoComRequestDeMusculacao() {
        var request = umRegistroAtividadeRequestParaMusculacao();
        var usuario = umUsuario();

        when(exercicioService.findById(1)).thenReturn(umExercicioMusculacao(1));

        service.editar(1, request, usuario);

        verify(exercicioService).findById(1);
        verify(locatorService).getRegistroAtividadeService(MUSCULACAO);
        verify(registroMusculacaoService).editarRegistro(1, request, usuario);
        verifyNoInteractions(registroAaerobicoService);
    }

    @Test
    void editar_deveChamarMetodoEditarDoRegistroAerobicoService_quandoSolicitadoComRequestDeAerobico() {
        var request = umRegistroAtividadeRequestParaAerobico();
        var usuario = umUsuario();

        when(exercicioService.findById(3)).thenReturn(umExercicioAerobico(3));

        service.editar(2, request, usuario);

        verify(exercicioService).findById(3);
        verify(locatorService).getRegistroAtividadeService(AEROBICO);
        verify(registroAaerobicoService).editarRegistro(2, request, usuario);
        verifyNoInteractions(registroMusculacaoService);
    }

    @Test
    void excluir_deveChamarMetodoExcluirRegistro_quandoSolicitadoComIdDeExercicioDeMusculacao() {
        var usuario = umUsuario();

        when(exercicioService.findById(2)).thenReturn(umExercicioMusculacao(1));

        service.excluir(1, 2, usuario);

        verify(exercicioService).findById(2);
        verify(locatorService).getRegistroAtividadeService(MUSCULACAO);
        verify(registroMusculacaoService).excluirRegistro(1, usuario);
    }

    @Test
    void excluir_deveChamarMetodoExcluirRegistro_quandoSolicitadoComIdDeExercicioDeAerobico() {
        var usuario = umUsuario();

        when(exercicioService.findById(2)).thenReturn(umExercicioAerobico(1));

        service.excluir(1, 2, usuario);

        verify(exercicioService).findById(2);
        verify(locatorService).getRegistroAtividadeService(AEROBICO);
        verify(registroAaerobicoService).excluirRegistro(1, usuario);
    }
}
