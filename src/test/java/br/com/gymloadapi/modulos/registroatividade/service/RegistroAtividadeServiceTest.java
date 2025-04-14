package br.com.gymloadapi.modulos.registroatividade.service;

import br.com.gymloadapi.modulos.comum.service.LocatorService;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.registroatividade.registrocardio.service.RegistroCardioService;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.service.RegistroCargaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento.HALTER;
import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.AEROBICO;
import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.MUSCULACAO;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioAerobico;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioMusculacao;
import static br.com.gymloadapi.modulos.registroatividade.helper.RegistroAtividadeHelper.*;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuario;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void salvar_deveChamarMetodoSalvarDoRegistroCargaService_quandoSolicitadoComRequestDeMusculacao() {
        var request = umRegistroAtividadeRequestParaMusculacao();
        var usuario = umUsuario();
        var exercicio = umExercicioMusculacao(1);

        when(exercicioService.findById(1)).thenReturn(exercicio);
        when(locatorService.getRegistroAtividadeService(MUSCULACAO)).thenReturn(registroCargaService);

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
        when(locatorService.getRegistroAtividadeService(AEROBICO)).thenReturn(registroCardioService);

        service.salvar(request, usuario);

        verify(exercicioService).findById(3);
        verify(locatorService).getRegistroAtividadeService(AEROBICO);
        verify(registroCardioService).salvarRegistro(request, exercicio, usuario);
        verifyNoInteractions(registroCargaService);
    }

    @Test
    @SuppressWarnings("LineLength")
    void buscarUltimoRegistroAtividade_deveChamarMetodoBuscarUltimoRegistroAtividadeDoRegistroCargaService_quandoSolicitadoComExercicioDeMusculacao() {
        var exercicio = umExercicioMusculacao(1);

        when(exercicioService.findById(1)).thenReturn(exercicio);
        when(locatorService.getRegistroAtividadeService(MUSCULACAO)).thenReturn(registroCargaService);
        when(registroCargaService.buscarUltimoRegistro(1, 2))
            .thenReturn(umRegistroAtividadeResponseComDadosDeMusculacao());

        var response = service.buscarUltimoRegistroAtividade(1, 2);
        assertAll(
            () -> assertEquals("22.5 (KG)", response.destaque()),
            () -> assertEquals(1, response.historicoRegistroAtividade().getFirst().id()),
            () -> assertEquals("SUPINO RETO", response.historicoRegistroAtividade().getFirst().exercicioNome()),
            () -> assertEquals("22.5 (KG)", response.historicoRegistroAtividade().getFirst().carga()),
            () -> assertEquals(HALTER, response.historicoRegistroAtividade().getFirst().tipoExercicio()),
            () -> assertEquals("Peitoral", response.historicoRegistroAtividade().getFirst().grupoMuscularNome()),
            () -> assertEquals(12, response.historicoRegistroAtividade().getFirst().qtdRepeticoes()),
            () -> assertEquals(4, response.historicoRegistroAtividade().getFirst().qtdSeries()),
            () -> assertEquals(LocalDate.of(2025, 4, 4), response.historicoRegistroAtividade().getFirst().dataCadastro())
        );

        verify(exercicioService).findById(1);
        verify(locatorService).getRegistroAtividadeService(MUSCULACAO);
        verify(registroCargaService).buscarUltimoRegistro(1, 2);
        verifyNoInteractions(registroCardioService);
    }

    @Test
    @SuppressWarnings("LineLength")
    void buscarUltimoRegistroAtividade_deveChamarMetodoBuscarUltimoRegistroAtividadeDoRegistroCardioService_quandoSolicitadoComRequestDeAerobico() {
        var exercicio = umExercicioAerobico(3);

        when(exercicioService.findById(3)).thenReturn(exercicio);
        when(locatorService.getRegistroAtividadeService(AEROBICO)).thenReturn(registroCardioService);
        when(registroCardioService.buscarUltimoRegistro(3, 2))
            .thenReturn(umRegistroAtividadeResponseComDadosDeAerobico());

        var response = service.buscarUltimoRegistroAtividade(3, 2);
        assertAll(
            () -> assertEquals("22.5 KM", response.destaque()),
            () -> assertEquals(2, response.historicoRegistroAtividade().getFirst().id()),
            () -> assertEquals("ESTEIRA", response.historicoRegistroAtividade().getFirst().exercicioNome()),
            () -> assertEquals(LocalDate.of(2025, 4, 4), response.historicoRegistroAtividade().getFirst().dataCadastro()),
            () -> assertEquals(22.5, response.historicoRegistroAtividade().getFirst().distancia()),
            () -> assertEquals(2.0, response.historicoRegistroAtividade().getFirst().duracao())
        );

        verify(exercicioService).findById(3);
        verify(locatorService).getRegistroAtividadeService(AEROBICO);
        verify(registroCardioService).buscarUltimoRegistro(3, 2);
        verifyNoInteractions(registroCargaService);
    }

    @Test
    @SuppressWarnings("LineLength")
    void buscarRegistroAtividadeCompleto_deveChamarMetodobuscarRegistroAtividadeCompletoDoRegistroCargaService_quandoSolicitadoComExercicioDeMusculacao() {
        var exercicio = umExercicioMusculacao(1);

        when(exercicioService.findById(1)).thenReturn(exercicio);
        when(locatorService.getRegistroAtividadeService(MUSCULACAO)).thenReturn(registroCargaService);
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
        when(locatorService.getRegistroAtividadeService(AEROBICO)).thenReturn(registroCardioService);
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
        when(locatorService.getRegistroAtividadeService(MUSCULACAO)).thenReturn(registroCargaService);

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
        when(locatorService.getRegistroAtividadeService(AEROBICO)).thenReturn(registroCardioService);

        service.editar(2, request, usuario);

        verify(exercicioService).findById(3);
        verify(locatorService).getRegistroAtividadeService(AEROBICO);
        verify(registroCardioService).editarRegistro(2, request, usuario);
        verifyNoInteractions(registroCargaService);
    }
}
