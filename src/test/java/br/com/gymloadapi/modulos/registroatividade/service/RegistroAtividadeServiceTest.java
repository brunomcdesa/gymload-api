package br.com.gymloadapi.modulos.registroatividade.service;

import br.com.gymloadapi.modulos.comum.service.LocatorService;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.registroatividade.registrocardio.service.RegistroCardioService;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.service.RegistroCargaService;
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
    private RegistroCargaService registroCargaService;
    @Mock
    private RegistroCardioService registroCardioService;

    @BeforeEach
    void setUp() {
        lenient().when(locatorService.getRegistroAtividadeService(MUSCULACAO)).thenReturn(registroCargaService);
        lenient().when(locatorService.getRegistroAtividadeService(AEROBICO)).thenReturn(registroCardioService);
    }

    @Test
    void salvar_deveChamarMetodoSalvarDoRegistroCargaService_quandoSolicitadoComRequestDeMusculacao() {
        var request = umRegistroAtividadeRequestParaMusculacao();
        var usuario = umUsuario();
        var exercicio = umExercicioMusculacao(1);

        when(exercicioService.findById(1)).thenReturn(exercicio);

        service.salvar(request, usuario);

        verify(exercicioService).findById(1);
        verify(locatorService).getRegistroAtividadeService(MUSCULACAO);
        verify(registroCargaService).salvarRegistro(request, exercicio, usuario);
        verifyNoInteractions(registroCardioService);
    }

    @Test
    void salvar_deveChamarMetodoSalvarDoRegistroCardioService_quandoSolicitadoComRequestDeAerobico() {
        var request = umRegistroAtividadeRequestParaAerobico();
        var usuario = umUsuario();
        var exercicio = umExercicioAerobico(3);

        when(exercicioService.findById(3)).thenReturn(exercicio);

        service.salvar(request, usuario);

        verify(exercicioService).findById(3);
        verify(locatorService).getRegistroAtividadeService(AEROBICO);
        verify(registroCardioService).salvarRegistro(request, exercicio, usuario);
        verifyNoInteractions(registroCargaService);
    }

    @Test
    void buscarDestaques_deveRetornarListaVazia_quandoNaoEncontrarNenhumExercicio() {
        when(exercicioService.findByIdIn(List.of(1, 2))).thenReturn(Collections.emptyList());

        assertTrue(service.buscarDestaques(umRegistroAtividadeFiltros(), 1).isEmpty());

        verify(exercicioService).findByIdIn(List.of(1, 2));
        verifyNoInteractions(locatorService, registroCargaService, registroCardioService);
    }

    @Test
    void buscarDestaques_deveRetornarDestaquesDosExercicios_quandoEncontrarExercicios() {
        when(exercicioService.findByIdIn(List.of(1, 2))).thenReturn(maisUmaListaDeExercicios());
        when(registroCargaService.buscarDestaque(1, 1)).thenReturn(umRegistroAtividadeResponseComDadosDeMusculacao());
        when(registroCardioService.buscarDestaque(2, 1)).thenReturn(umRegistroAtividadeResponseComDadosDeAerobico());

        var responses = service.buscarDestaques(umRegistroAtividadeFiltros(), 1);
        assertAll(
            () -> assertEquals(1, responses.getFirst().exercicioId()),
            () -> assertEquals("22.5 (KG)", responses.getFirst().destaque()),
            () -> assertEquals("20.0 (KG)", responses.getFirst().ultimaCarga()),
            () -> assertNull(responses.getFirst().ultimaDistancia()),
            () -> assertEquals(2, responses.getLast().exercicioId()),
            () -> assertEquals("22.5 KM", responses.getLast().destaque()),
            () -> assertNull(responses.getLast().ultimaCarga()),
            () -> assertEquals("11,25 KM", responses.getLast().ultimaDistancia())
        );

        verify(exercicioService).findByIdIn(List.of(1, 2));
        verify(locatorService).getRegistroAtividadeService(MUSCULACAO);
        verify(locatorService).getRegistroAtividadeService(AEROBICO);
        verify(registroCargaService).buscarDestaque(1, 1);
        verify(registroCardioService).buscarDestaque(2, 1);
    }

    @Test
    @SuppressWarnings("LineLength")
    void buscarRegistroAtividadeCompleto_deveChamarMetodobuscarRegistroAtividadeCompletoDoRegistroCargaService_quandoSolicitadoComExercicioDeMusculacao() {
        var exercicio = umExercicioMusculacao(1);

        when(exercicioService.findById(1)).thenReturn(exercicio);
        when(registroCargaService.buscarHistoricoRegistroCompleto(1, 2))
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
        verify(registroCargaService).buscarHistoricoRegistroCompleto(1, 2);
        verifyNoInteractions(registroCardioService);
    }

    @Test
    @SuppressWarnings("LineLength")
    void buscarRegistroAtividadeCompleto_deveChamarMetodobuscarRegistroAtividadeCompletoDoRegistroCardioService_quandoSolicitadoComRequestDeCardio() {
        var exercicio = umExercicioAerobico(3);

        when(exercicioService.findById(3)).thenReturn(exercicio);
        when(registroCardioService.buscarHistoricoRegistroCompleto(3, 2))
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
        verify(registroCardioService).buscarHistoricoRegistroCompleto(3, 2);
        verifyNoInteractions(registroCargaService);
    }

    @Test
    void editar_deveChamarMetodoEditarDoRegistroCargaService_quandoSolicitadoComRequestDeMusculacao() {
        var request = umRegistroAtividadeRequestParaMusculacao();
        var usuario = umUsuario();

        when(exercicioService.findById(1)).thenReturn(umExercicioMusculacao(1));

        service.editar(1, request, usuario);

        verify(exercicioService).findById(1);
        verify(locatorService).getRegistroAtividadeService(MUSCULACAO);
        verify(registroCargaService).editarRegistro(1, request, usuario);
        verifyNoInteractions(registroCardioService);
    }

    @Test
    void editar_deveChamarMetodoEditarDoRegistroCardioService_quandoSolicitadoComRequestDeAerobico() {
        var request = umRegistroAtividadeRequestParaAerobico();
        var usuario = umUsuario();

        when(exercicioService.findById(3)).thenReturn(umExercicioAerobico(3));

        service.editar(2, request, usuario);

        verify(exercicioService).findById(3);
        verify(locatorService).getRegistroAtividadeService(AEROBICO);
        verify(registroCardioService).editarRegistro(2, request, usuario);
        verifyNoInteractions(registroCargaService);
    }

    @Test
    void excluir_deveChamarMetodoExcluirRegistro_quandoSolicitadoComIdDeExercicioDeMusculacao() {
        var usuario = umUsuario();

        when(exercicioService.findById(2)).thenReturn(umExercicioMusculacao(1));

        service.excluir(1, 2, usuario);

        verify(exercicioService).findById(2);
        verify(locatorService).getRegistroAtividadeService(MUSCULACAO);
        verify(registroCargaService).excluirRegistro(1, usuario);
    }

    @Test
    void excluir_deveChamarMetodoExcluirRegistro_quandoSolicitadoComIdDeExercicioDeAerobico() {
        var usuario = umUsuario();

        when(exercicioService.findById(2)).thenReturn(umExercicioAerobico(1));

        service.excluir(1, 2, usuario);

        verify(exercicioService).findById(2);
        verify(locatorService).getRegistroAtividadeService(AEROBICO);
        verify(registroCardioService).excluirRegistro(1, usuario);
    }
}
