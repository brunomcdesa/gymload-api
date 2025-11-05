package br.com.gymloadapi.modulos.exercicio.service;

import br.com.gymloadapi.modulos.cache.config.CacheConfig;
import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.exercicio.mapper.ExercicioMapper;
import br.com.gymloadapi.modulos.exercicio.mapper.ExercicioMapperImpl;
import br.com.gymloadapi.modulos.exercicio.model.ExercicioVariacao;
import br.com.gymloadapi.modulos.exercicio.repository.ExercicioVariacaoRepository;
import br.com.gymloadapi.modulos.tipovariacao.service.TipoVariacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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

import jakarta.validation.ConstraintViolationException;
import java.util.Objects;
import java.util.Optional;

import static br.com.gymloadapi.modulos.cache.utils.CacheUtils.getCachesExerciciosVariacoes;
import static br.com.gymloadapi.modulos.comum.enums.EAcao.EDICAO;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.*;
import static br.com.gymloadapi.modulos.tipovariacao.helper.TipoVariacaoHelper.outroTipoVariacao;
import static br.com.gymloadapi.modulos.tipovariacao.helper.TipoVariacaoHelper.umTipoVariacao;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import({ExercicioVariacaoServiceTest.TestServiceConfig.class, CacheConfig.class})
class ExercicioVariacaoServiceTest {

    @TestConfiguration
    static class TestServiceConfig {
        @Bean
        public ExercicioMapper exercicioMapper() {
            return new ExercicioMapperImpl();
        }

        @Bean
        public ExercicioVariacaoService exercicioService(ExercicioMapper exercicioMapper, ExercicioService exercicioService,
                                                         ExercicioVariacaoRepository repository,
                                                         TipoVariacaoService tipoVariacaoService,
                                                         ExercicioVariacaoHistoricoService exercicioVariacaoHistoricoService) {
            return new ExercicioVariacaoService(exercicioMapper, exercicioService, repository, tipoVariacaoService,
                exercicioVariacaoHistoricoService);
        }
    }

    @Autowired
    private ExercicioVariacaoService service;
    @Autowired
    private CacheManager cacheManager;
    @MockitoBean
    private ExercicioVariacaoRepository repository;
    @MockitoBean
    private ExercicioService exercicioService;
    @MockitoBean
    private ExercicioVariacaoHistoricoService historicoService;
    @MockitoBean
    private TipoVariacaoService tipoVariacaoService;
    @Captor
    private ArgumentCaptor<ExercicioVariacao> exercicioVariacaoCaptor;

    @BeforeEach
    void setUp() {
        getCachesExerciciosVariacoes().stream()
            .map(cacheManager::getCache)
            .filter(Objects::nonNull)
            .forEach(Cache::clear);
    }

    @Test
    void salvar_deveSalvarNovaVariacao_quandoSolicitado() {
        when(exercicioService.findById(1)).thenReturn(umExercicioMusculacao(1));
        when(repository.existsByTipoVariacao_IdAndExercicio_Id(1, 1)).thenReturn(false);
        when(tipoVariacaoService.buscarPorId(1)).thenReturn(umTipoVariacao());

        service.salvar(umExercicioVariacaoRequestComTipoVariacao(), 1);

        verify(exercicioService).findById(1);
        verify(repository).existsByTipoVariacao_IdAndExercicio_Id(1, 1);
        verify(tipoVariacaoService).buscarPorId(1);
        verify(repository).save(exercicioVariacaoCaptor.capture());
        verifyNoMoreInteractions(repository);

        var exercicioVariacao = exercicioVariacaoCaptor.getValue();
        assertAll(
            () -> assertEquals("SUPINO RETO - Halter", exercicioVariacao.getNome()),
            () -> assertEquals(1, exercicioVariacao.getTipoVariacao().getId()),
            () -> assertEquals(1, exercicioVariacao.getExercicio().getId()),
            () -> assertEquals(1, exercicioVariacao.getUsuarioCadastroId())
        );
    }

    @Test
    void salvar_deveSalvarNovaVariacao_quandoSolicitadoParaExercicioCalistenia() {
        var exercicio = umExercicioCalistenia(1);
        exercicio.setPossuiVariacao(true);

        when(exercicioService.findById(1)).thenReturn(exercicio);
        when(repository.existsByNomeIgnoreCase("Abdominal Supra")).thenReturn(false);

        service.salvar(umExercicioVariacaoRequestSemTipoVariacao(), 1);

        verify(exercicioService).findById(1);
        verify(repository).existsByNomeIgnoreCase("Abdominal Supra");
        verify(repository).save(exercicioVariacaoCaptor.capture());
        verifyNoMoreInteractions(repository);

        var exercicioVariacao = exercicioVariacaoCaptor.getValue();
        assertAll(
            () -> assertEquals("Abdominal Supra", exercicioVariacao.getNome()),
            () -> assertNull(exercicioVariacao.getTipoVariacao()),
            () -> assertEquals(1, exercicioVariacao.getExercicio().getId()),
            () -> assertEquals(1, exercicioVariacao.getUsuarioCadastroId())
        );
    }

    @Test
    void salvar_deveLancarException_quandoAplicarValidacoesEmGruopoParaAerobico() {
        var request = umExercicioVariacaoRequest(1, "teste", 1);

        when(exercicioService.findById(1)).thenReturn(umExercicioAerobico(1));

        var exception = assertThrowsExactly(
            ConstraintViolationException.class,
            () -> service.salvar(request, 1)
        );
        assertThat(exception.getMessage())
            .contains("tipoVariacaoId: deve ser nulo",
                "nome: deve ser nulo");

        verify(exercicioService).findById(1);
        verifyNoInteractions(repository);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void salvar_deveLancarException_quandoAplicarValidacoesEmGruopoParaCalistenia(String nomeInvalido) {
        var request = umExercicioVariacaoRequest(1, nomeInvalido, 2);

        when(exercicioService.findById(1)).thenReturn(umExercicioCalistenia(1));

        var exception = assertThrowsExactly(
            ConstraintViolationException.class,
            () -> service.salvar(request, 1)
        );
        assertThat(exception.getMessage())
            .contains("tipoVariacaoId: deve ser nulo",
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
            .contains("tipoVariacaoId: é obrigatório.",
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
        var request = umExercicioVariacaoRequestComTipoVariacao();

        when(exercicioService.findById(1)).thenReturn(umExercicioMusculacao(1));
        when(repository.existsByTipoVariacao_IdAndExercicio_Id(1, 1)).thenReturn(true);

        var exception = assertThrowsExactly(
            ValidacaoException.class,
            () -> service.salvar(request, 1)
        );
        assertEquals("Já existe uma variação igual para este exercício.", exception.getMessage());

        verify(exercicioService).findById(1);
        verify(repository).existsByTipoVariacao_IdAndExercicio_Id(1, 1);
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
            () -> service.salvar(umExercicioVariacaoRequestSemTipoVariacao(), 1)
        );
        assertEquals("Já existe uma variação igual para este exercício.", exception.getMessage());

        verify(exercicioService).findById(1);
        verify(repository).existsByNomeIgnoreCase("Abdominal Supra");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void salvar_deveLimparOsCaches_quandoExecutado() {
        when(exercicioService.findById(1)).thenReturn(umExercicioMusculacao(1));

        service.buscarVariacoesDoExercicio(1);
        service.buscarVariacoesDoExercicio(2);

        service.salvar(umExercicioVariacaoRequestComTipoVariacao(), 1);

        service.buscarVariacoesDoExercicio(1);
        service.buscarVariacoesDoExercicio(2);

        verify(exercicioService).findById(1);
        verify(repository, times(2)).findAllByExercicioId(1);
        verify(repository, times(2)).findAllByExercicioId(2);
    }

    @Test
    void buscarVariacoesDoExercicio_deveRetornarListaVazia_quandoNaoEncontrarVariacoesDoExercicio() {
        when(repository.findAllByExercicioId(1)).thenReturn(emptyList());

        assertTrue(service.buscarVariacoesDoExercicio(1).isEmpty());

        verify(repository).findAllByExercicioId(1);
    }

    @Test
    void buscarVariacoesDoExercicio_deveRetornarListaDeVariacoes_quandoEncontrarVariacoesDoExercicio() {
        when(repository.findAllByExercicioId(1)).thenReturn(umaListaExercicioVariacao());

        var variacoes = service.buscarVariacoesDoExercicio(1);
        assertAll(
            () -> assertEquals(1, variacoes.getFirst().id()),
            () -> assertEquals("SUPINO RETO - Halter", variacoes.getFirst().nome()),
            () -> assertEquals(1, variacoes.getFirst().tipoVariacao().id()),
            () -> assertEquals(2, variacoes.getLast().id()),
            () -> assertEquals("SUPINO RETO - Barra", variacoes.getLast().nome()),
            () -> assertEquals(2, variacoes.getLast().tipoVariacao().id())
        );

        verify(repository).findAllByExercicioId(1);
    }

    @Test
    void buscarVariacoesDoExercicio_deveRetornarDadosDoChace_quandoSolicitadoVariasVezes() {
        service.buscarVariacoesDoExercicio(1);
        service.buscarVariacoesDoExercicio(1);
        service.buscarVariacoesDoExercicio(1);
        service.buscarVariacoesDoExercicio(1);

        verify(repository).findAllByExercicioId(1);
    }

    @Test
    void editarVariacao_deveLancarException_quandoNaoEncontrarVariacao() {
        when(repository.findCompleteById(1)).thenReturn(Optional.empty());

        var exception = assertThrowsExactly(
            NotFoundException.class,
            () -> service.editarVariacao(1, umExercicioVariacaoRequestComDados(), umUsuarioAdmin())
        );
        assertEquals("Variação de exercício não encontrada.", exception.getMessage());

        verify(repository).findCompleteById(1);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(historicoService);
    }

    @Test
    void editarVariacao_deveLancarException_quandoAplicarValidacaoDeGruposExercicioMusculacao() {
        when(repository.findCompleteById(1)).thenReturn(Optional.of(umExercicioVariacao()));

        var exception = assertThrowsExactly(
            ConstraintViolationException.class,
            () -> service.editarVariacao(1, umExercicioVariacaoRequest(1, "teste", null),
                umUsuarioAdmin())
        );
        assertThat(exception.getMessage())
            .contains("tipoVariacaoId: é obrigatório",
                "nome: deve ser nulo");

        verify(repository).findCompleteById(1);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(historicoService);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void editarVariacao_deveLancarException_quandoAplicarValidacaoDeGruposExercicioCalistenia(String nomeInvalido) {
        var variacao = umExercicioVariacao();
        variacao.setExercicio(umExercicioCalistenia(1));

        when(repository.findCompleteById(1)).thenReturn(Optional.of(variacao));

        var exception = assertThrowsExactly(
            ConstraintViolationException.class,
            () -> service.editarVariacao(1, umExercicioVariacaoRequest(1, nomeInvalido, 1),
                umUsuarioAdmin())
        );
        assertThat(exception.getMessage())
            .contains("tipoVariacaoId: deve ser nulo",
                "nome: é obrigatório.");

        verify(repository).findCompleteById(1);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(historicoService);
    }

    @Test
    void editarVariacao_deveLancarException_quandoAplicarValidacaoDeGruposExercicioAerobico() {
        var variacao = umExercicioVariacao();
        variacao.setExercicio(umExercicioAerobico(1));

        when(repository.findCompleteById(1)).thenReturn(Optional.of(variacao));

        var exception = assertThrowsExactly(
            ConstraintViolationException.class,
            () -> service.editarVariacao(1, umExercicioVariacaoRequest(1, "teste", 1),
                umUsuarioAdmin())
        );
        assertThat(exception.getMessage())
            .contains("tipoVariacaoId: deve ser nulo",
                "nome: deve ser nulo");

        verify(repository).findCompleteById(1);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(historicoService);
    }

    @Test
    void editarVariacao_deveLancarException_quandoExercicioNaoPuderTerVariacao() {
        var exercicio = umExercicioMusculacao(1);
        exercicio.setPossuiVariacao(false);
        var variacao = umExercicioVariacao();
        variacao.setExercicio(exercicio);

        when(repository.findCompleteById(1)).thenReturn(Optional.of(variacao));

        var exception = assertThrowsExactly(
            ValidacaoException.class,
            () -> service.editarVariacao(1, umExercicioVariacaoRequest(1, null, 1),
                umUsuarioAdmin())
        );
        assertEquals("Este exercício não está permitido para ter variações.", exception.getMessage());

        verify(repository).findCompleteById(1);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(historicoService);
    }

    @Test
    void editarVariacao_deveLancarException_quandoExistirVariacaoComMesmoTipoEquipamento() {
        when(repository.findCompleteById(1)).thenReturn(Optional.of(umExercicioVariacao()));
        when(repository.existsByTipoVariacao_IdAndExercicio_Id(1, 1)).thenReturn(true);

        var exception = assertThrowsExactly(
            ValidacaoException.class,
            () -> service.editarVariacao(1, umExercicioVariacaoRequest(1, null, 1),
                umUsuarioAdmin())
        );
        assertEquals("Já existe uma variação igual para este exercício.", exception.getMessage());

        verify(repository).findCompleteById(1);
        verify(repository).existsByTipoVariacao_IdAndExercicio_Id(1, 1);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(historicoService);
    }

    @Test
    void editarVariacao_deveLancarException_quandoExistirVariacaoComMesmoNome() {
        var exercicio = umExercicioCalistenia(1);
        exercicio.setPossuiVariacao(true);
        var variacao = umExercicioVariacao();
        variacao.setExercicio(exercicio);

        when(repository.findCompleteById(1)).thenReturn(Optional.of(variacao));
        when(repository.existsByNomeIgnoreCase("TESTE")).thenReturn(true);

        var exception = assertThrowsExactly(
            ValidacaoException.class,
            () -> service.editarVariacao(1, umExercicioVariacaoRequest(1, "TESTE", null),
                umUsuarioAdmin())
        );
        assertEquals("Já existe uma variação igual para este exercício.", exception.getMessage());

        verify(repository).findCompleteById(1);
        verify(repository).existsByNomeIgnoreCase("TESTE");
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(historicoService);
    }

    @Test
    void editarVariacao_deveEditarVariacao_quandoVariacaoMusculacao() {
        var variacao = umExercicioVariacao();

        when(repository.findCompleteById(1)).thenReturn(Optional.of(variacao));
        when(repository.existsByTipoVariacao_IdAndExercicio_Id(2, 1)).thenReturn(false);
        when(tipoVariacaoService.buscarPorId(2)).thenReturn(outroTipoVariacao());

        assertDoesNotThrow(() -> service.editarVariacao(1, umExercicioVariacaoRequest(1, null, 2),
            umUsuarioAdmin()));

        verify(repository).findCompleteById(1);
        verify(repository).existsByTipoVariacao_IdAndExercicio_Id(2, 1);
        verify(tipoVariacaoService).buscarPorId(2);
        verify(repository).save(variacao);
        verify(historicoService).salvar(variacao, 1, EDICAO);
        verifyNoMoreInteractions(repository);

        assertAll(
            () -> assertEquals(2, variacao.getTipoVariacao().getId()),
            () -> assertEquals("SUPINO RETO - Barra", variacao.getNome())
        );
    }

    @Test
    void editarVariacao_deveEditarVariacao_quandoVariacaoCalistenia() {
        var exercicio = umExercicioCalistenia(1);
        exercicio.setPossuiVariacao(true);
        var variacao = umExercicioVariacao();
        variacao.setExercicio(exercicio);
        variacao.setTipoVariacao(null);

        when(repository.findCompleteById(1)).thenReturn(Optional.of(variacao));
        when(repository.existsByNomeIgnoreCase("TESTE")).thenReturn(false);

        assertDoesNotThrow(() -> service.editarVariacao(1, umExercicioVariacaoRequest(1, "TESTE", null),
            umUsuarioAdmin()));

        verify(repository).findCompleteById(1);
        verify(repository).existsByNomeIgnoreCase("TESTE");
        verify(repository).save(variacao);
        verify(historicoService).salvar(variacao, 1, EDICAO);
        verifyNoMoreInteractions(repository);

        assertAll(
            () -> assertNull(variacao.getTipoVariacao()),
            () -> assertEquals("TESTE", variacao.getNome())
        );
    }

    @Test
    void editarVariacao_deveLimparOsCaches_quandoExecutado() {
        when(repository.findCompleteById(1)).thenReturn(Optional.of(umExercicioVariacao()));
        when(repository.existsByTipoVariacao_IdAndExercicio_Id(2, 1)).thenReturn(false);
        when(tipoVariacaoService.buscarPorId(2)).thenReturn(outroTipoVariacao());

        service.buscarVariacoesDoExercicio(1);
        service.buscarVariacoesDoExercicio(2);

        service.editarVariacao(1, umExercicioVariacaoRequest(1, null, 2), umUsuarioAdmin());

        service.buscarVariacoesDoExercicio(1);
        service.buscarVariacoesDoExercicio(2);

        verify(repository).findCompleteById(1);
        verify(repository, times(2)).findAllByExercicioId(1);
        verify(repository, times(2)).findAllByExercicioId(2);
    }
}
