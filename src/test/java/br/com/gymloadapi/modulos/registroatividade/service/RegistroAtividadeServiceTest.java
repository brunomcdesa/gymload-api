package br.com.gymloadapi.modulos.registroatividade.service;

import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.exercicio.repository.ExercicioVariacaoRepository;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.registroatividade.registroaerobico.service.RegistroAerobicoService;
import br.com.gymloadapi.modulos.registroatividade.registrocalistenia.service.RegistroCalisteniaService;
import br.com.gymloadapi.modulos.registroatividade.registromusculacao.service.RegistroMusculacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento.HALTER;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.*;
import static br.com.gymloadapi.modulos.registroatividade.helper.RegistroAtividadeHelper.*;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuario;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistroAtividadeServiceTest {

    @InjectMocks
    private RegistroAtividadeService service;
    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private ExercicioService exercicioService;
    @Mock
    private ExercicioVariacaoRepository exercicioVariacaoRepository;
    @Mock
    private RegistroMusculacaoService registroMusculacaoService;
    @Mock
    private RegistroAerobicoService registroAerobicoService;
    @Mock
    private RegistroCalisteniaService registroCalisteniaService;

    @BeforeEach
    void setUp() {
        when(applicationContext.getBean(RegistroMusculacaoService.class)).thenReturn(registroMusculacaoService);
        when(applicationContext.getBean(RegistroAerobicoService.class)).thenReturn(registroAerobicoService);
        when(applicationContext.getBean(RegistroCalisteniaService.class)).thenReturn(registroCalisteniaService);
        service.init();
    }

    @Test
    void salvar_deveChamarMetodoSalvarDoRegistroMusculacaoService_quandoSolicitadoComRequestDeMusculacao() {
        var request = umRegistroAtividadeRequestParaMusculacao();
        var usuario = umUsuario();
        var exercicio = umExercicioMusculacao(1);

        when(exercicioService.findById(1)).thenReturn(exercicio);

        service.salvar(request, usuario);

        verify(exercicioService).findById(1);
        verify(registroMusculacaoService).salvarRegistro(request, exercicio, null, usuario);
        verifyNoInteractions(registroAerobicoService, registroCalisteniaService);
    }

    @Test
    void salvar_deveChamarMetodoSalvarComVariacao_quandoExercicioPossuiVariacaoEVariacaoIdValido() {
        var request = umRegistroAtividadeRequestParaMusculacaoComVariacao();
        var usuario = umUsuario();
        var exercicio = umExercicioMusculacaoComVariacao(1);
        var variacao = umExercicioVariacao();

        when(exercicioService.findById(1)).thenReturn(exercicio);
        when(exercicioVariacaoRepository.findByIdAndExercicio_Id(10, 1)).thenReturn(Optional.of(variacao));

        service.salvar(request, usuario);

        verify(exercicioService).findById(1);
        verify(exercicioVariacaoRepository).findByIdAndExercicio_Id(10, 1);
        verify(registroMusculacaoService).salvarRegistro(request, exercicio, variacao, usuario);
        verifyNoInteractions(registroAerobicoService, registroCalisteniaService);
    }

    @Test
    void salvar_deveLancarException_quandoExercicioPossuiVariacaoEVariacaoIdNull() {
        var request = umRegistroAtividadeRequestParaMusculacao();
        var usuario = umUsuario();
        var exercicio = umExercicioMusculacaoComVariacao(1);

        when(exercicioService.findById(1)).thenReturn(exercicio);

        var exception = assertThrowsExactly(
            ValidacaoException.class,
            () -> service.salvar(request, usuario)
        );
        assertEquals("Este exercício possui variações. Informe a variação para registrar a atividade.",
            exception.getMessage());

        verify(exercicioService).findById(1);
        verifyNoInteractions(registroMusculacaoService, registroAerobicoService, registroCalisteniaService);
    }

    @Test
    void salvar_deveLancarException_quandoVariacaoNaoPertenceAoExercicio() {
        var request = umRegistroAtividadeRequestParaMusculacaoComVariacao();
        var usuario = umUsuario();
        var exercicio = umExercicioMusculacaoComVariacao(1);

        when(exercicioService.findById(1)).thenReturn(exercicio);
        when(exercicioVariacaoRepository.findByIdAndExercicio_Id(10, 1)).thenReturn(Optional.empty());

        var exception = assertThrowsExactly(
            ValidacaoException.class,
            () -> service.salvar(request, usuario)
        );
        assertEquals("A variação informada não pertence a este exercício.", exception.getMessage());

        verify(exercicioService).findById(1);
        verify(exercicioVariacaoRepository).findByIdAndExercicio_Id(10, 1);
        verifyNoInteractions(registroMusculacaoService, registroAerobicoService, registroCalisteniaService);
    }

    @Test
    void salvar_deveLancarException_quandoExercicioNaoPossuiVariacaoMasVariacaoIdInformado() {
        var request = umRegistroAtividadeRequestParaMusculacaoComVariacao();
        var usuario = umUsuario();
        var exercicio = umExercicioMusculacao(1);

        when(exercicioService.findById(1)).thenReturn(exercicio);

        var exception = assertThrowsExactly(
            ValidacaoException.class,
            () -> service.salvar(request, usuario)
        );
        assertEquals("Este exercício não possui variações. Não informe uma variação ao registrar a atividade.",
            exception.getMessage());

        verify(exercicioService).findById(1);
        verifyNoInteractions(registroMusculacaoService, registroAerobicoService, registroCalisteniaService);
    }

    @Test
    void salvar_deveChamarMetodoSalvarDoRegistroAerobicoService_quandoSolicitadoComRequestDeAerobico() {
        var request = umRegistroAtividadeRequestParaAerobico();
        var usuario = umUsuario();
        var exercicio = umExercicioAerobico(3);

        when(exercicioService.findById(3)).thenReturn(exercicio);

        service.salvar(request, usuario);

        verify(exercicioService).findById(3);
        verify(registroAerobicoService).salvarRegistro(request, exercicio, null, usuario);
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
        verify(registroCalisteniaService).salvarRegistro(request, exercicio, null, usuario);
        verifyNoInteractions(registroMusculacaoService, registroAerobicoService);
    }

    @Test
    void buscarDestaques_deveRetornarListaVazia_quandoNaoEncontrarNenhumExercicio() {
        when(exercicioService.findByIdIn(List.of(1, 2, 3))).thenReturn(Collections.emptyList());

        assertTrue(service.buscarDestaques(umRegistroAtividadeFiltros(), 1).isEmpty());

        verify(exercicioService).findByIdIn(List.of(1, 2, 3));
        verifyNoInteractions(registroMusculacaoService, registroAerobicoService, registroCalisteniaService);
    }

    @Test
    void buscarDestaques_deveRetornarDestaquesDosExercicios_quandoEncontrarExercicios() {
        when(exercicioService.findByIdIn(List.of(1, 2, 3))).thenReturn(maisUmaListaDeExercicios());
        when(registroMusculacaoService.buscarDestaque(1, 1)).thenReturn(umRegistroAtividadeResponseComDadosDeMusculacao());
        when(registroAerobicoService.buscarDestaque(2, 1)).thenReturn(umRegistroAtividadeResponseComDadosDeAerobico());
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
        verify(registroMusculacaoService).buscarDestaque(1, 1);
        verify(registroAerobicoService).buscarDestaque(2, 1);
        verify(registroCalisteniaService).buscarDestaque(3, 1);
    }

    @Test
    void buscarDestaques_deveRetornarResponseSemDados_quandoExercicioPossuirVariacoes() {
        var exercicioMusculacaoComVariacao = umExercicioMusculacaoComVariacao(1);
        when(exercicioService.findByIdIn(List.of(1, 2, 3))).thenReturn(List.of(exercicioMusculacaoComVariacao));

        var responses = service.buscarDestaques(umRegistroAtividadeFiltros(), 1);

        assertAll(
            () -> assertEquals(1, responses.getFirst().exercicioId()),
            () -> assertNull(responses.getFirst().destaque()),
            () -> assertNull(responses.getFirst().ultimoPeso()),
            () -> assertNull(responses.getFirst().ultimaDistancia()),
            () -> assertNull(responses.getFirst().ultimaQtdMaxRepeticoes())
        );

        verify(exercicioService).findByIdIn(List.of(1, 2, 3));
        verifyNoInteractions(registroMusculacaoService, registroAerobicoService, registroCalisteniaService);
    }

    @Test
    @SuppressWarnings("LineLength")
    void buscarRegistroAtividadeCompleto_deveChamarMetodoBuscarRegistroAtividadeCompletoDoRegistroMusculacaoService_quandoSolicitadoComExercicioDeMusculacao() {
        var exercicio = umExercicioMusculacao(1);

        when(exercicioService.findById(1)).thenReturn(exercicio);
        when(registroMusculacaoService.buscarHistoricoRegistroCompleto(1, null, 2))
            .thenReturn(List.of(umHistoricoRegistroAtividadeResponseDeMusculacao()));

        var response = service.buscarRegistroAtividadeCompleto(1, null, 2);
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
        verify(registroMusculacaoService).buscarHistoricoRegistroCompleto(1, null, 2);
        verifyNoInteractions(registroAerobicoService, registroCalisteniaService);
    }

    @Test
    void buscarRegistroAtividadeCompleto_deveChamarStrategyComVariacaoId_quandoVariacaoIdInformado() {
        var exercicio = umExercicioMusculacaoComVariacao(1);

        when(exercicioService.findById(1)).thenReturn(exercicio);
        when(registroMusculacaoService.buscarHistoricoRegistroCompleto(1, 10, 2))
            .thenReturn(List.of(umHistoricoRegistroAtividadeResponseDeMusculacao()));

        var response = service.buscarRegistroAtividadeCompleto(1, 10, 2);
        assertEquals(1, response.size());

        verify(exercicioService).findById(1);
        verify(registroMusculacaoService).buscarHistoricoRegistroCompleto(1, 10, 2);
        verifyNoInteractions(registroAerobicoService, registroCalisteniaService);
    }

    @Test
    void buscarRegistroAtividadeCompleto_deveRetornarListaVazia_quandoExercicioPossuiVariacaoEVariacaoIdNull() {
        var exercicio = umExercicioMusculacaoComVariacao(1);

        when(exercicioService.findById(1)).thenReturn(exercicio);

        assertTrue(service.buscarRegistroAtividadeCompleto(1, null, 2).isEmpty());

        verify(exercicioService).findById(1);
        verifyNoInteractions(registroMusculacaoService, registroAerobicoService, registroCalisteniaService);
    }

    @Test
    @SuppressWarnings("LineLength")
    void buscarRegistroAtividadeCompleto_deveChamarMetodoBuscarRegistroAtividadeCompletoDoRegistroAerobicoService_quandoSolicitadoComRequestDeAerobico() {
        var exercicio = umExercicioAerobico(2);

        when(exercicioService.findById(2)).thenReturn(exercicio);
        when(registroAerobicoService.buscarHistoricoRegistroCompleto(2, null, 2))
            .thenReturn(List.of(umHistoricoRegistroAtividadeResponseDeAerobico()));

        var response = service.buscarRegistroAtividadeCompleto(2, null, 2);
        assertAll(
            () -> assertEquals(2, response.getFirst().id()),
            () -> assertEquals("ESTEIRA", response.getFirst().exercicioNome()),
            () -> assertEquals(LocalDate.of(2025, 4, 4), response.getFirst().dataCadastro()),
            () -> assertEquals(22.5, response.getFirst().distancia()),
            () -> assertEquals(2.0, response.getFirst().duracao())
        );

        verify(exercicioService).findById(2);
        verify(registroAerobicoService).buscarHistoricoRegistroCompleto(2, null, 2);
        verifyNoInteractions(registroMusculacaoService, registroCalisteniaService);
    }

    @Test
    @SuppressWarnings("LineLength")
    void buscarRegistroAtividadeCompleto_deveChamarMetodoBuscarRegistroAtividadeCompletoDoRegistroCalisteniaService_quandoSolicitadoComExercicioDeCalistenia() {
        var exercicio = umExercicioCalistenia(3);

        when(exercicioService.findById(3)).thenReturn(exercicio);
        when(registroCalisteniaService.buscarHistoricoRegistroCompleto(3, null, 2))
            .thenReturn(List.of(umHistoricoRegistroAtividadeResponseDeCalistenia()));

        var response = service.buscarRegistroAtividadeCompleto(3, null, 2);
        assertAll(
            () -> assertEquals(3, response.getFirst().id()),
            () -> assertEquals("Abdominal Supra", response.getFirst().exercicioNome()),
            () -> assertEquals("Abdomen", response.getFirst().grupoMuscularNome()),
            () -> assertEquals(30, response.getFirst().qtdRepeticoes()),
            () -> assertEquals(4, response.getFirst().qtdSeries()),
            () -> assertEquals(LocalDate.of(2025, 4, 4), response.getFirst().dataCadastro())
        );

        verify(exercicioService).findById(3);
        verify(registroCalisteniaService).buscarHistoricoRegistroCompleto(3, null, 2);
        verifyNoInteractions(registroAerobicoService, registroMusculacaoService);
    }

    @Test
    void editar_deveChamarMetodoEditarDoRegistroMusculacaoService_quandoSolicitadoComRequestDeMusculacao() {
        var request = umRegistroAtividadeRequestParaMusculacao();
        var usuario = umUsuario();

        when(exercicioService.findById(1)).thenReturn(umExercicioMusculacao(1));

        service.editar(1, request, usuario);

        verify(exercicioService).findById(1);
        verify(registroMusculacaoService).editarRegistro(1, request, usuario);
        verifyNoInteractions(registroAerobicoService, registroCalisteniaService);
    }

    @Test
    void editar_deveChamarMetodoEditarDoRegistroAerobicoService_quandoSolicitadoComRequestDeAerobico() {
        var request = umRegistroAtividadeRequestParaAerobico();
        var usuario = umUsuario();

        when(exercicioService.findById(3)).thenReturn(umExercicioAerobico(3));

        service.editar(2, request, usuario);

        verify(exercicioService).findById(3);
        verify(registroAerobicoService).editarRegistro(2, request, usuario);
        verifyNoInteractions(registroMusculacaoService, registroCalisteniaService);
    }

    @Test
    void editar_deveChamarMetodoEditarDoRegistroCalisteniaService_quandoSolicitadoComRequestDeCalistenia() {
        var request = umRegistroAtividadeRequestParaCalistenia();
        var usuario = umUsuario();

        when(exercicioService.findById(4)).thenReturn(umExercicioCalistenia(4));

        service.editar(1, request, usuario);

        verify(exercicioService).findById(4);
        verify(registroCalisteniaService).editarRegistro(1, request, usuario);
        verifyNoInteractions(registroAerobicoService, registroMusculacaoService);
    }

    @Test
    void excluir_deveChamarMetodoExcluirRegistro_quandoSolicitadoComIdDeExercicioDeMusculacao() {
        var usuario = umUsuario();

        when(exercicioService.findById(2)).thenReturn(umExercicioMusculacao(1));

        service.excluir(1, 2, usuario);

        verify(exercicioService).findById(2);
        verify(registroMusculacaoService).excluirRegistro(1, usuario);
        verifyNoInteractions(registroAerobicoService, registroCalisteniaService);
    }

    @Test
    void excluir_deveChamarMetodoExcluirRegistro_quandoSolicitadoComIdDeExercicioDeAerobico() {
        var usuario = umUsuario();

        when(exercicioService.findById(2)).thenReturn(umExercicioAerobico(1));

        service.excluir(1, 2, usuario);

        verify(exercicioService).findById(2);
        verify(registroAerobicoService).excluirRegistro(1, usuario);
        verifyNoInteractions(registroMusculacaoService, registroCalisteniaService);
    }

    @Test
    void excluir_deveChamarMetodoExcluirRegistro_quandoSolicitadoComIdDeExercicioDeCalsitenia() {
        var usuario = umUsuario();

        when(exercicioService.findById(3)).thenReturn(umExercicioCalistenia(3));

        service.excluir(1, 3, usuario);

        verify(exercicioService).findById(3);
        verify(registroCalisteniaService).excluirRegistro(1, usuario);
        verifyNoInteractions(registroAerobicoService, registroMusculacaoService);
    }

    @Test
    void repetirUltimoRegistro_deveChamarMetodoRepetirUltimoRegistro_quandoSolicitadoComIdDeExercicioDeMusculacao() {
        var usuario = umUsuario();
        var exercicio = umExercicioMusculacao(1);

        when(exercicioService.findById(1)).thenReturn(exercicio);

        service.repetirUltimoRegistro(1, usuario);

        verify(exercicioService).findById(1);
        verify(registroMusculacaoService).repetirUltimoRegistro(exercicio, usuario);
        verifyNoInteractions(registroAerobicoService, registroCalisteniaService);
    }

    @Test
    void repetirUltimoRegistro_deveChamarMetodoRepetirUltimoRegistro_quandoSolicitadoComIdDeExercicioDeAerobico() {
        var usuario = umUsuario();
        var exercicio = umExercicioAerobico(2);

        when(exercicioService.findById(2)).thenReturn(exercicio);

        service.repetirUltimoRegistro(2, usuario);

        verify(exercicioService).findById(2);
        verify(registroAerobicoService).repetirUltimoRegistro(exercicio, usuario);
        verifyNoInteractions(registroMusculacaoService, registroCalisteniaService);
    }

    @Test
    void repetirUltimoRegistro_deveChamarMetodoRepetirUltimoRegistro_quandoSolicitadoComIdDeExercicioDeCalistenia() {
        var usuario = umUsuario();
        var exercicio = umExercicioCalistenia(3);

        when(exercicioService.findById(3)).thenReturn(exercicio);

        service.repetirUltimoRegistro(3, usuario);

        verify(exercicioService).findById(3);
        verify(registroCalisteniaService).repetirUltimoRegistro(exercicio, usuario);
        verifyNoInteractions(registroMusculacaoService, registroAerobicoService);
    }

    @Test
    void repetirRegistro_deveChamarMetodoRepetirRegistro_quandoSolicitadoComIdDeExercicioDeMusculacao() {
        var exercicio = umExercicioMusculacao(1);

        when(exercicioService.findById(1)).thenReturn(exercicio);

        service.repetirRegistro(1, 1);

        verify(exercicioService).findById(1);
        verify(registroMusculacaoService).repetirRegistro(1);
        verifyNoInteractions(registroAerobicoService, registroCalisteniaService);
    }

    @Test
    void repetirRegistro_deveChamarMetodoRepetirRegistro_quandoSolicitadoComIdDeExercicioDeAerobico() {
        var exercicio = umExercicioAerobico(2);

        when(exercicioService.findById(2)).thenReturn(exercicio);

        service.repetirRegistro(2, 2);

        verify(exercicioService).findById(2);
        verify(registroAerobicoService).repetirRegistro(2);
        verifyNoInteractions(registroMusculacaoService, registroCalisteniaService);
    }

    @Test
    void repetirRegistro_deveChamarMetodoRepetirRegistro_quandoSolicitadoComIdDeExercicioDeCalistenia() {
        var exercicio = umExercicioCalistenia(3);

        when(exercicioService.findById(3)).thenReturn(exercicio);

        service.repetirRegistro(3, 3);

        verify(exercicioService).findById(3);
        verify(registroCalisteniaService).repetirRegistro(3);
        verifyNoInteractions(registroMusculacaoService, registroAerobicoService);
    }

    @Test
    void moverRegistros_deveMoverRegistros_quandoSolicitado() {
        var exercicio = umExercicioMusculacao(1);
        var variacaoDestino = umExercicioVariacao();

        when(exercicioService.findById(1)).thenReturn(exercicio);
        when(exercicioVariacaoRepository.findByIdAndExercicio_Id(5, 1)).thenReturn(Optional.of(variacaoDestino));
        when(exercicioVariacaoRepository.findByExercicio_IdAndPadraoTrue(1)).thenReturn(empty());

        var request = umMoverRegistrosRequest();
        var usuario = umUsuario();
        service.moverRegistros(request, usuario);

        verify(exercicioService).findById(1);
        verify(exercicioVariacaoRepository).findByIdAndExercicio_Id(5, 1);
        verify(registroMusculacaoService).moverRegistros(List.of(10, 20), 5, 2);
        verify(exercicioVariacaoRepository).findByExercicio_IdAndPadraoTrue(1);
        verifyNoInteractions(registroAerobicoService, registroCalisteniaService);
    }

    @Test
    void moverRegistros_deveDeletarVariacaoPadrao_quandoTodosOsRegistrosForemMovidos() {
        var exercicio = umExercicioMusculacao(1);
        var variacaoDestino = umExercicioVariacao();
        var variacaoPadrao = umExercicioVariacaoPadrao();

        when(exercicioService.findById(1)).thenReturn(exercicio);
        when(exercicioVariacaoRepository.findByIdAndExercicio_Id(5, 1)).thenReturn(Optional.of(variacaoDestino));
        when(exercicioVariacaoRepository.findByExercicio_IdAndPadraoTrue(1)).thenReturn(Optional.of(variacaoPadrao));
        when(registroMusculacaoService.contarRegistrosPorVariacao(99)).thenReturn(0L);

        service.moverRegistros(umMoverRegistrosRequest(), umUsuario());

        verify(registroMusculacaoService).moverRegistros(List.of(10, 20), 5, 2);
        verify(registroMusculacaoService).contarRegistrosPorVariacao(99);
        verify(exercicioVariacaoRepository).deleteById(99);
    }

    @Test
    void moverRegistros_naoDeveDeletarVariacaoPadrao_quandoAindaExistiremRegistros() {
        var exercicio = umExercicioMusculacao(1);
        var variacaoDestino = umExercicioVariacao();
        var variacaoPadrao = umExercicioVariacaoPadrao();

        when(exercicioService.findById(1)).thenReturn(exercicio);
        when(exercicioVariacaoRepository.findByIdAndExercicio_Id(5, 1)).thenReturn(Optional.of(variacaoDestino));
        when(exercicioVariacaoRepository.findByExercicio_IdAndPadraoTrue(1)).thenReturn(Optional.of(variacaoPadrao));
        when(registroMusculacaoService.contarRegistrosPorVariacao(99)).thenReturn(3L);

        service.moverRegistros(umMoverRegistrosRequest(), umUsuario());

        verify(registroMusculacaoService).contarRegistrosPorVariacao(99);
        verify(exercicioVariacaoRepository, never()).deleteById(anyInt());
    }

    @Test
    void moverRegistros_deveLancarException_quandoVariacaoDestinoNaoPertenceAoExercicio() {
        when(exercicioService.findById(1)).thenReturn(umExercicioMusculacao(1));
        when(exercicioVariacaoRepository.findByIdAndExercicio_Id(5, 1)).thenReturn(empty());

        var exception = assertThrowsExactly(
            ValidacaoException.class,
            () -> service.moverRegistros(umMoverRegistrosRequest(), umUsuario())
        );
        assertEquals("A variação de destino não pertence a este exercício.", exception.getMessage());

        verify(exercicioVariacaoRepository).findByIdAndExercicio_Id(5, 1);
        verifyNoInteractions(registroMusculacaoService, registroAerobicoService, registroCalisteniaService);
    }
}
