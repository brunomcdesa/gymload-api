package br.com.gymloadapi.modulos.tipovariacao.service;

import br.com.gymloadapi.modulos.cache.config.CacheConfig;
import br.com.gymloadapi.modulos.comum.dto.SelectResponse;
import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.tipovariacao.dto.TipoVariacaoResponse;
import br.com.gymloadapi.modulos.tipovariacao.mapper.TipoVariacaoMapper;
import br.com.gymloadapi.modulos.tipovariacao.mapper.TipoVariacaoMapperImpl;
import br.com.gymloadapi.modulos.tipovariacao.model.TipoVariacao;
import br.com.gymloadapi.modulos.tipovariacao.repository.TipoVariacaoRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static br.com.gymloadapi.modulos.cache.utils.CacheUtils.getCachesTiposVariacoes;
import static br.com.gymloadapi.modulos.comum.enums.EAcao.CADASTRO;
import static br.com.gymloadapi.modulos.comum.enums.EAcao.EDICAO;
import static br.com.gymloadapi.modulos.tipovariacao.helper.TipoVariacaoHelper.umTipoVariacao;
import static br.com.gymloadapi.modulos.tipovariacao.helper.TipoVariacaoHelper.umTipoVariacaoRequest;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import({TipoVariacaoServiceTest.TestServiceConfig.class, CacheConfig.class})
class TipoVariacaoServiceTest {

    @TestConfiguration
    static class TestServiceConfig {
        @Bean
        public TipoVariacaoMapper tipoVariacaoMapper() {
            return new TipoVariacaoMapperImpl();
        }

        @Bean
        public TipoVariacaoService exercicioService(TipoVariacaoRepository repository, TipoVariacaoMapper tipoVariacaoMapper,
                                                    TipoVariacaoHistoricoService historicoService) {
            return new TipoVariacaoService(repository, tipoVariacaoMapper, historicoService);
        }
    }

    @Autowired
    private TipoVariacaoService service;
    @Autowired
    private CacheManager cacheManager;
    @MockitoBean
    private TipoVariacaoRepository repository;
    @MockitoBean
    private TipoVariacaoHistoricoService historicoService;
    @Captor
    private ArgumentCaptor<TipoVariacao> tipoVariacaoCaptor;

    @BeforeEach
    void setUp() {
        getCachesTiposVariacoes().stream()
            .map(cacheManager::getCache)
            .filter(Objects::nonNull)
            .forEach(Cache::clear);
    }

    @Test
    void salvar_deveLancarExcption_quandoExistirTipoDeVariacaoComMesmoNome() {
        when(repository.existsByNomeIgnoreCase("Halter")).thenReturn(true);

        var exception = assertThrowsExactly(
            ValidacaoException.class,
            () -> service.salvar(umTipoVariacaoRequest(), umUsuarioAdmin())
        );
        assertEquals("Já existe uma variação com este nome.", exception.getMessage());

        verify(repository).existsByNomeIgnoreCase("Halter");
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(historicoService);
    }

    @Test
    void salvar_deveSalvarNovoTipoDeVariacao_quandoNaoExistirTipoVariacaoComMesmoNome() {
        when(repository.existsByNomeIgnoreCase("Halter")).thenReturn(false);

        assertDoesNotThrow(() -> service.salvar(umTipoVariacaoRequest(), umUsuarioAdmin()));

        verify(repository).existsByNomeIgnoreCase("Halter");
        verify(repository).save(tipoVariacaoCaptor.capture());
        verify(historicoService).salvar(any(TipoVariacao.class), eq(1), eq(CADASTRO));

        var tipoVariacao = tipoVariacaoCaptor.getValue();
        assertAll(
            () -> assertEquals("Halter", tipoVariacao.getNome()),
            () -> assertEquals(1, tipoVariacao.getUsuarioCadastroId()),
            () -> assertEquals("Usuario Admin", tipoVariacao.getUsuarioCadastroNome()),
            () -> assertNotNull(tipoVariacao.getDataCadastro())
        );
    }

    @Test
    void salvar_deveRemoverTodosOsCachesDeTiposVariacao_quandoSalvarUmNovoTipoDeVariacao() {
        when(repository.existsByNomeIgnoreCase(anyString())).thenReturn(false);

        service.buscarTodos();
        service.getSelect();

        service.salvar(umTipoVariacaoRequest(), umUsuarioAdmin());

        service.buscarTodos();
        service.getSelect();

        verify(repository, times(4)).findAll();
        verify(repository).existsByNomeIgnoreCase(anyString());
        verify(repository).save(any(TipoVariacao.class));
        verify(historicoService).salvar(any(TipoVariacao.class), eq(1), eq(CADASTRO));
    }

    @Test
    void buscarTodos_deveRetornarListaVazia_quandoNaoEncontrarNenhumTipoVariacao() {
        when(repository.findAll()).thenReturn(emptyList());

        assertTrue(service.buscarTodos().isEmpty());

        verify(repository).findAll();
    }

    @Test
    void buscarTodos_deveRetornarListaDeTiposVariacoes_quandoEncontrarTiposVariacoes() {
        when(repository.findAll()).thenReturn(List.of(umTipoVariacao()));

        assertThat(service.buscarTodos())
            .extracting(TipoVariacaoResponse::id, TipoVariacaoResponse::nome,
                TipoVariacaoResponse::dataCadastro)
            .containsExactly(tuple(1, "Halter", LocalDateTime.of(2025, 8, 21, 10, 30)));

        verify(repository).findAll();
    }

    @Test
    void buscarTodos_deveRetornarDadosDoCache_quandoSolicitadoVariasVezes() {
        when(repository.findAll()).thenReturn(emptyList());

        service.buscarTodos();
        service.buscarTodos();
        service.buscarTodos();

        verify(repository).findAll();
    }

    @Test
    void editar_deveLancarException_quandoPossuirOutroTipoDeVariacaoComOMesmoNome() {
        when(repository.existsByNomeIgnoreCase("Halter")).thenReturn(true);

        var exception = assertThrowsExactly(
            ValidacaoException.class,
            () -> service.editar(1, umTipoVariacaoRequest(), umUsuarioAdmin())
        );
        assertEquals("Já existe uma variação com este nome.", exception.getMessage());

        verify(repository).existsByNomeIgnoreCase("Halter");
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(historicoService);
    }

    @Test
    void editar_deveLancarException_quandoNaoEncontrarTipoVariacaoComMesmoId() {
        when(repository.existsByNomeIgnoreCase("Halter")).thenReturn(false);
        when(repository.findById(1)).thenReturn(Optional.empty());

        var exception = assertThrowsExactly(
            NotFoundException.class,
            () -> service.editar(1, umTipoVariacaoRequest(), umUsuarioAdmin())
        );
        assertEquals("Tipo de Variação não encontrado.", exception.getMessage());

        verify(repository).existsByNomeIgnoreCase("Halter");
        verify(repository).findById(1);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(historicoService);
    }

    @Test
    void editar_deveEditarTipoDeVariacao_quandoNaoExistirOutroTipoVariacaoComMesmoNome() {
        var tipoVariacao = umTipoVariacao();
        tipoVariacao.setNome("Barra");

        when(repository.existsByNomeIgnoreCase("Halter")).thenReturn(false);
        when(repository.findById(1)).thenReturn(Optional.of(tipoVariacao));

        assertDoesNotThrow(() -> service.editar(1, umTipoVariacaoRequest(), umUsuarioAdmin()));

        assertAll(
            () -> assertEquals("Halter", tipoVariacao.getNome()),
            () -> assertEquals(1, tipoVariacao.getUsuarioCadastroId()),
            () -> assertEquals("Usuario Admin", tipoVariacao.getUsuarioCadastroNome()),
            () -> assertNotNull(tipoVariacao.getDataCadastro())
        );

        verify(repository).existsByNomeIgnoreCase("Halter");
        verify(repository).findById(1);
        verify(repository).save(tipoVariacao);
        verify(historicoService).salvar(any(TipoVariacao.class), eq(1), eq(EDICAO));
    }

    @Test
    void editar_deveLimparTodosOsCachesDeTiposDeVariacao_quandoEditarUmTipoDeVariacao() {
        when(repository.findById(1)).thenReturn(Optional.of(umTipoVariacao()));
        when(repository.existsByNomeIgnoreCase(anyString())).thenReturn(false);

        service.buscarTodos();
        service.getSelect();

        service.editar(1, umTipoVariacaoRequest(), umUsuarioAdmin());

        service.buscarTodos();
        service.getSelect();

        verify(repository, times(4)).findAll();
        verify(repository).existsByNomeIgnoreCase(anyString());
        verify(repository).findById(1);
        verify(repository).save(any(TipoVariacao.class));
        verify(historicoService).salvar(any(TipoVariacao.class), eq(1), eq(EDICAO));
    }

    @Test
    void getSelect_deveRetornarUmaListaVazia_quandoNaoPossuirTiposDeVariacoesCadastradas() {
        when(repository.findAll()).thenReturn(emptyList());

        assertTrue(service.getSelect().isEmpty());

        verify(repository).findAll();
    }

    @Test
    void getSelect_deveRetornarUmaListaDeTiposDeVariacao_quandoPossuirTiposDeVariacoesCadastradas() {
        when(repository.findAll()).thenReturn(List.of(umTipoVariacao()));

        assertThat(service.getSelect())
            .extracting(SelectResponse::value, SelectResponse::label)
            .containsExactly(tuple(1, "Halter"));

        verify(repository).findAll();
    }

    @Test
    void getSelect_deveRetornarDadosDoCache_quandoSolicitadoVariasVezes() {
        when(repository.findAll()).thenReturn(emptyList());

        service.getSelect();
        service.getSelect();
        service.getSelect();
        service.getSelect();

        verify(repository).findAll();
    }
}
