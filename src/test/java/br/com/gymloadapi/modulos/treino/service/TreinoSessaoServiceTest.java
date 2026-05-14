package br.com.gymloadapi.modulos.treino.service;

import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.registroatividade.service.RegistroAtividadeService;
import br.com.gymloadapi.modulos.treino.model.TreinoSessao;
import br.com.gymloadapi.modulos.treino.repository.TreinoSessaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static br.com.gymloadapi.modulos.comum.enums.EAcao.CADASTRO;
import static br.com.gymloadapi.modulos.comum.enums.ESituacao.ATIVO;
import static br.com.gymloadapi.modulos.treino.helper.TreinoHelper.umTreino;
import static br.com.gymloadapi.modulos.treino.helper.TreinoSessaoHelper.umaTreinoSessao;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import(TreinoSessaoServiceTest.TestServiceConfig.class)
class TreinoSessaoServiceTest {

    @TestConfiguration
    static class TestServiceConfig {

        @Bean
        public TreinoSessaoService treinoSessaoService(TreinoSessaoRepository repository,
                                                       TreinoSessaoHistoricoService historicoService,
                                                       TreinoService treinoService,
                                                       RegistroAtividadeService registroAtividadeService) {
            return new TreinoSessaoService(repository, historicoService, treinoService, registroAtividadeService);
        }
    }

    @Autowired
    private TreinoSessaoService service;
    @MockitoBean
    private TreinoSessaoRepository repository;
    @MockitoBean
    private TreinoSessaoHistoricoService historicoService;
    @MockitoBean
    private TreinoService treinoService;
    @MockitoBean
    private RegistroAtividadeService registroAtividadeService;
    @Captor
    private ArgumentCaptor<TreinoSessao> captor;

    @Test
    void finalizar_deveSalvarSessaoEHistorico_quandoTemRegistroHoje() {
        var treino = umTreino(ATIVO);
        when(treinoService.findByIdAndUsuarioId(1, 1)).thenReturn(treino);
        when(registroAtividadeService.existeRegistroHojeParaExercicios(List.of(1, 2), 1)).thenReturn(true);
        when(repository.save(any(TreinoSessao.class))).thenReturn(umaTreinoSessao());

        var usuario = umUsuarioAdmin();
        service.finalizar(1, usuario);

        verify(treinoService).findByIdAndUsuarioId(1, 1);
        verify(registroAtividadeService).existeRegistroHojeParaExercicios(List.of(1, 2), 1);
        verify(repository).save(captor.capture());
        verify(historicoService).salvar(any(TreinoSessao.class), eq(1), eq(CADASTRO));

        var sessao = captor.getValue();
        assertAll(
            () -> assertEquals(LocalDate.now(), sessao.getDataSessao()),
            () -> assertEquals(treino, sessao.getTreino()),
            () -> assertEquals(usuario, sessao.getUsuario())
        );
    }

    @Test
    void finalizar_deveLancarValidacaoException_quandoNaoTemRegistroHoje() {
        var usuario = umUsuarioAdmin();
        when(treinoService.findByIdAndUsuarioId(1, 1)).thenReturn(umTreino(ATIVO));
        when(registroAtividadeService.existeRegistroHojeParaExercicios(List.of(1, 2), 1)).thenReturn(false);

        var exception = assertThrowsExactly(
            ValidacaoException.class,
            () -> service.finalizar(1, usuario)
        );
        assertEquals("Registre pelo menos um exercício antes de finalizar o treino.", exception.getMessage());

        verify(treinoService).findByIdAndUsuarioId(1, 1);
        verify(registroAtividadeService).existeRegistroHojeParaExercicios(List.of(1, 2), 1);
        verifyNoInteractions(repository, historicoService);
    }

    @Test
    void finalizar_deveLancarNotFoundException_quandoTreinoNaoPertenceAoUsuario() {
        when(treinoService.findByIdAndUsuarioId(99, 1))
            .thenThrow(new NotFoundException("Treino não encontrado."));

        var usuario = umUsuarioAdmin();
        var exception = assertThrowsExactly(
            NotFoundException.class,
            () -> service.finalizar(99, usuario)
        );
        assertEquals("Treino não encontrado.", exception.getMessage());

        verify(treinoService).findByIdAndUsuarioId(99, 1);
        verifyNoInteractions(repository, registroAtividadeService, historicoService);
    }
}
