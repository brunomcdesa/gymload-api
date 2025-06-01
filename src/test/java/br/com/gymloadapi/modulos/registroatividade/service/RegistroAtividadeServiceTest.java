package br.com.gymloadapi.modulos.registroatividade.service;

import br.com.gymloadapi.modulos.comum.service.LocatorService;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.registroatividade.registroaerobico.service.RegistroAaerobicoService;
import br.com.gymloadapi.modulos.registroatividade.registrocalistenia.service.RegistroCalisteniaService;
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
import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.*;
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
    @Mock
    private RegistroCalisteniaService registroCalisteniaService;

    @BeforeEach
    void setUp() {
        lenient().when(locatorService.getRegistroAtividadeService(MUSCULACAO)).thenReturn(registroMusculacaoService);
        lenient().when(locatorService.getRegistroAtividadeService(AEROBICO)).thenReturn(registroAaerobicoService);
        lenient().when(locatorService.getRegistroAtividadeService(CALISTENIA)).thenReturn(registroCalisteniaService);
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
        verifyNoInteractions(registroAaerobicoService, registroCalisteniaService);
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
        verifyNoInteractions(registroMusculacaoService, registroCalisteniaService);
    }

    @Test
    void salvar_deveChamarMetodoSalvarDoRegistroCalisteniaService_quandoSolicitadoComRequestDeCalistenia() {
        var request = umRegistroAtividadeRequestParaCalistenia();
        var usuario = umUsuario();
        var exercicio = umExercicioCalistenia(4);

        when(exercicioService.findById(4)).thenReturn(exercicio);

        service.salvar(request, usuario);

        verify(exercicioService).findById(4);
        verify(locatorService).getRegistroAtividadeService(CALISTENIA);
        verify(registroCalisteniaService).salvarRegistro(request, exercicio, usuario);
        verifyNoInteractions(registroMusculacaoService, registroAaerobicoService);
    }

    @Test
    void buscarDestaques_deveRetornarListaVazia_quandoNaoEncontrarNenhumExercicio() {
        when(exercicioService.findByIdIn(List.of(1, 2, 3))).thenReturn(Collections.emptyList());

        assertTrue(service.buscarDestaques(umRegistroAtividadeFiltros(), 1).isEmpty());

        verify(exercicioService).findByIdIn(List.of(1, 2, 3));
        verifyNoInteractions(locatorService, registroMusculacaoService, registroAaerobicoService, registroCalisteniaService);
    }

    @Test
    void buscarDestaques_deveRetornarDestaquesDosExercicios_quandoEncontrarExercicios() {
        when(exercicioService.findByIdIn(List.of(1, 2, 3))).thenReturn(maisUmaListaDeExercicios());
        when(registroMusculacaoService.buscarDestaque(1, 1)).thenReturn(umRegistroAtividadeResponseComDadosDeMusculacao());
        when(registroAaerobicoService.buscarDestaque(2, 1)).thenReturn(umRegistroAtividadeResponseComDadosDeAerobico());
        when(registroCalisteniaService.buscarDestaque(3, 1)).thenReturn(umRegistroAtividadeResponseComDadosDeCalistenia());

        var responses = service.buscarDestaques(umRegistroAtividadeFiltros(), 1);
        assertAll(
            () -> assertEquals(1, responses.getFirst().exercicioId()),
            () -> assertEquals("22.5 (KG)", responses.getFirst().destaque()),
            () -> assertEquals("20.0 (KG)", responses.getFirst().ultimoPeso()),
            () -> assertNull(responses.getFirst().ultimaDistancia()),
            () -> assertNull(responses.getFirst().ultimaQtdMaxRepeticoes()),
            () -> assertEquals(2, responses.get(1).exercicioId()),
            () -> assertEquals("22.5 KM", responses.get(1).destaque()),
            () -> assertNull(responses.get(1).ultimoPeso()),
            () -> assertNull(responses.get(1).ultimaQtdMaxRepeticoes()),
            () -> assertEquals("11,25 KM", responses.get(1).ultimaDistancia()),
            () -> assertEquals(3, responses.getLast().exercicioId()),
            () -> assertEquals("30 Reps", responses.getLast().destaque()),
            () -> assertNull(responses.getLast().ultimoPeso()),
            () -> assertNull(responses.getLast().ultimaDistancia()),
            () -> assertEquals(30, responses.getLast().ultimaQtdMaxRepeticoes())
        );

        verify(exercicioService).findByIdIn(List.of(1, 2, 3));
        verify(locatorService).getRegistroAtividadeService(MUSCULACAO);
        verify(locatorService).getRegistroAtividadeService(AEROBICO);
        verify(registroMusculacaoService).buscarDestaque(1, 1);
        verify(registroAaerobicoService).buscarDestaque(2, 1);
        verify(registroCalisteniaService).buscarDestaque(3, 1);
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
            () -> assertEquals(HALTER, response.getFirst().tipoEquipamento()),
            () -> assertEquals("Peitoral", response.getFirst().grupoMuscularNome()),
            () -> assertEquals(12, response.getFirst().qtdRepeticoes()),
            () -> assertEquals(4, response.getFirst().qtdSeries()),
            () -> assertEquals(LocalDate.of(2025, 4, 4), response.getFirst().dataCadastro())
        );

        verify(exercicioService).findById(1);
        verify(locatorService).getRegistroAtividadeService(MUSCULACAO);
        verify(registroMusculacaoService).buscarHistoricoRegistroCompleto(1, 2);
        verifyNoInteractions(registroAaerobicoService, registroCalisteniaService);
    }

    @Test
    @SuppressWarnings("LineLength")
    void buscarRegistroAtividadeCompleto_deveChamarMetodoBuscarRegistroAtividadeCompletoDoRegistroAerobicoService_quandoSolicitadoComRequestDeAerobico() {
        var exercicio = umExercicioAerobico(2);

        when(exercicioService.findById(2)).thenReturn(exercicio);
        when(registroAaerobicoService.buscarHistoricoRegistroCompleto(2, 2))
            .thenReturn(List.of(umHistoricoRegistroAtividadeResponseDeAerobico()));

        var response = service.buscarRegistroAtividadeCompleto(2, 2);
        assertAll(
            () -> assertEquals(2, response.getFirst().id()),
            () -> assertEquals("ESTEIRA", response.getFirst().exercicioNome()),
            () -> assertEquals(LocalDate.of(2025, 4, 4), response.getFirst().dataCadastro()),
            () -> assertEquals(22.5, response.getFirst().distancia()),
            () -> assertEquals(2.0, response.getFirst().duracao())
        );

        verify(exercicioService).findById(2);
        verify(locatorService).getRegistroAtividadeService(AEROBICO);
        verify(registroAaerobicoService).buscarHistoricoRegistroCompleto(2, 2);
        verifyNoInteractions(registroMusculacaoService, registroCalisteniaService);
    }

    @Test
    @SuppressWarnings("LineLength")
    void buscarRegistroAtividadeCompleto_deveChamarMetodoBuscarRegistroAtividadeCompletoDoRegistroCalisteniaService_quandoSolicitadoComExercicioDeCalistenia() {
        var exercicio = umExercicioCalistenia(3);

        when(exercicioService.findById(3)).thenReturn(exercicio);
        when(registroCalisteniaService.buscarHistoricoRegistroCompleto(3, 2))
            .thenReturn(List.of(umHistoricoRegistroAtividadeResponseDeCalistenia()));

        var response = service.buscarRegistroAtividadeCompleto(3, 2);
        assertAll(
            () -> assertEquals(3, response.getFirst().id()),
            () -> assertEquals("Abdominal Supra", response.getFirst().exercicioNome()),
            () -> assertEquals("Abdomen", response.getFirst().grupoMuscularNome()),
            () -> assertEquals(30, response.getFirst().qtdRepeticoes()),
            () -> assertEquals(4, response.getFirst().qtdSeries()),
            () -> assertEquals(LocalDate.of(2025, 4, 4), response.getFirst().dataCadastro())
        );

        verify(exercicioService).findById(3);
        verify(locatorService).getRegistroAtividadeService(CALISTENIA);
        verify(registroCalisteniaService).buscarHistoricoRegistroCompleto(3, 2);
        verifyNoInteractions(registroAaerobicoService, registroMusculacaoService);
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
        verifyNoInteractions(registroAaerobicoService, registroCalisteniaService);
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
        verifyNoInteractions(registroMusculacaoService, registroCalisteniaService);
    }

    @Test
    void editar_deveChamarMetodoEditarDoRegistroCalisteniaService_quandoSolicitadoComRequestDeCalistenia() {
        var request = umRegistroAtividadeRequestParaCalistenia();
        var usuario = umUsuario();

        when(exercicioService.findById(4)).thenReturn(umExercicioCalistenia(4));

        service.editar(1, request, usuario);

        verify(exercicioService).findById(4);
        verify(locatorService).getRegistroAtividadeService(CALISTENIA);
        verify(registroCalisteniaService).editarRegistro(1, request, usuario);
        verifyNoInteractions(registroAaerobicoService, registroMusculacaoService);
    }

    @Test
    void excluir_deveChamarMetodoExcluirRegistro_quandoSolicitadoComIdDeExercicioDeMusculacao() {
        var usuario = umUsuario();

        when(exercicioService.findById(2)).thenReturn(umExercicioMusculacao(1));

        service.excluir(1, 2, usuario);

        verify(exercicioService).findById(2);
        verify(locatorService).getRegistroAtividadeService(MUSCULACAO);
        verify(registroMusculacaoService).excluirRegistro(1, usuario);
        verifyNoInteractions(registroAaerobicoService, registroCalisteniaService);
    }

    @Test
    void excluir_deveChamarMetodoExcluirRegistro_quandoSolicitadoComIdDeExercicioDeAerobico() {
        var usuario = umUsuario();

        when(exercicioService.findById(2)).thenReturn(umExercicioAerobico(1));

        service.excluir(1, 2, usuario);

        verify(exercicioService).findById(2);
        verify(locatorService).getRegistroAtividadeService(AEROBICO);
        verify(registroAaerobicoService).excluirRegistro(1, usuario);
        verifyNoInteractions(registroMusculacaoService, registroCalisteniaService);
    }

    @Test
    void excluir_deveChamarMetodoExcluirRegistro_quandoSolicitadoComIdDeExercicioDeCalsitenia() {
        var usuario = umUsuario();

        when(exercicioService.findById(3)).thenReturn(umExercicioCalistenia(3));

        service.excluir(1, 3, usuario);

        verify(exercicioService).findById(3);
        verify(locatorService).getRegistroAtividadeService(CALISTENIA);
        verify(registroCalisteniaService).excluirRegistro(1, usuario);
        verifyNoInteractions(registroAaerobicoService, registroMusculacaoService);
    }
}
