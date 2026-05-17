package br.com.gymloadapi.modulos.exercicio.service;

import br.com.gymloadapi.modulos.comum.enums.EAcao;
import br.com.gymloadapi.modulos.comum.enums.ETipoExercicio;
import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioVariacaoRequest;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioVariacaoResponse;
import br.com.gymloadapi.modulos.exercicio.mapper.ExercicioMapper;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.exercicio.model.ExercicioVariacao;
import br.com.gymloadapi.modulos.exercicio.repository.ExercicioVariacaoRepository;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.strategy.IRegistroAtividadeStrategy;
import br.com.gymloadapi.modulos.tipovariacao.dto.TipoVariacaoResponse;
import br.com.gymloadapi.modulos.tipovariacao.model.TipoVariacao;
import br.com.gymloadapi.modulos.tipovariacao.service.TipoVariacaoService;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

import static br.com.gymloadapi.modulos.cache.utils.CacheUtils.CACHE_EXERCICIOS_VARIACOES_POR_EXERCICIO_ID;
import static br.com.gymloadapi.modulos.comum.enums.EAcao.CADASTRO;
import static br.com.gymloadapi.modulos.comum.enums.EAcao.EDICAO;
import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.*;
import static br.com.gymloadapi.modulos.comum.utils.MapUtils.mapNull;
import static br.com.gymloadapi.modulos.comum.utils.MapUtils.mapNullWithBackup;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class ExercicioVariacaoService {

    private static final String MSG_ERRO_VARIACAO_EXISTENTE = "Já existe uma variação igual para este exercício.";

    private final ExercicioMapper exercicioMapper;
    private final ExercicioService exercicioService;
    private final ExercicioVariacaoRepository repository;
    private final TipoVariacaoService tipoVariacaoService;
    private final ExercicioVariacaoHistoricoService historicoService;
    private final ApplicationContext applicationContext;

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CACHE_EXERCICIOS_VARIACOES_POR_EXERCICIO_ID, allEntries = true)
    })
    public void salvar(ExercicioVariacaoRequest request, Integer usuarioAutenticadoId) {
        var exercicio = exercicioService.findById(request.exercicioBaseId());
        this.aplicarValidacoes(request, exercicio);

        if (!repository.existsByExercicio_Id(exercicio.getId())) {
            this.migrarRegistrosPadraoSeNecessario(exercicio, usuarioAutenticadoId);
        }

        var tipoVariacao = mapNull(request.tipoVariacaoId(), tipoVariacaoService::buscarPorId);
        var nomeVariacao = mapNullWithBackup(tipoVariacao,
            _tipoVariacao -> this.getNomeVariacao(exercicio.getNome(), _tipoVariacao.getNome()),
            request.nome());

        var variacao = exercicioMapper.mapToExercicioVariacao(exercicio, usuarioAutenticadoId,
            tipoVariacao, nomeVariacao);

        this.saveComHistorico(variacao, usuarioAutenticadoId, CADASTRO);
    }

    @Cacheable(value = CACHE_EXERCICIOS_VARIACOES_POR_EXERCICIO_ID, key = "#exercicioId + '-' + #usuarioId")
    public List<ExercicioVariacaoResponse> buscarVariacoesDoExercicio(Integer exercicioId, Integer usuarioId) {
        var variacoes = repository.findAllByExercicioId(exercicioId);
        if (variacoes.isEmpty()) {
            return List.of();
        }

        var tipoExercicio = variacoes.getFirst().getExercicio().getTipoExercicio();
        var strategy = this.getStrategyByTipoExercicio(tipoExercicio);

        return variacoes.stream()
            .map(variacao -> this.montarResponse(variacao, tipoExercicio, strategy, usuarioId))
            .toList();
    }

    @Caching(evict = {
        @CacheEvict(value = CACHE_EXERCICIOS_VARIACOES_POR_EXERCICIO_ID, allEntries = true)
    })
    public void editarVariacao(Integer id, ExercicioVariacaoRequest request,
                               Usuario usuarioAutenticado) {
        var exercicioVariacao = this.findCompleteById(id);
        this.aplicarValidacoes(request, exercicioVariacao.getExercicio());
        var tipoVariacao = mapNull(request.tipoVariacaoId(), tipoVariacaoService::buscarPorId);
        var nomeVariacao = mapNullWithBackup(tipoVariacao,
            _tipoVariacao -> this.getNomeVariacao(exercicioVariacao.getExercicioNome(), _tipoVariacao.getNome()),
            request.nome());

        exercicioMapper.editarVariacao(tipoVariacao, nomeVariacao, exercicioVariacao);

        this.saveComHistorico(exercicioVariacao, usuarioAutenticado.getId(), EDICAO);
    }

    private ExercicioVariacaoResponse montarResponse(ExercicioVariacao variacao, ETipoExercicio tipoExercicio,
                                                     IRegistroAtividadeStrategy strategy, Integer usuarioId) {
        var destaque = strategy.buscarDestaquePorVariacao(variacao.getExercicio().getId(), variacao.getId(), usuarioId);

        return new ExercicioVariacaoResponse(
            variacao.getId(),
            variacao.getNome(),
            this.mapTipoVariacaoResponse(variacao.getTipoVariacao()),
            tipoExercicio == MUSCULACAO ? this.getUltimoValor(destaque, RegistroAtividadeResponse::ultimoPeso) : null,
            tipoExercicio == AEROBICO ? this.getUltimoValor(destaque, RegistroAtividadeResponse::ultimaDistancia) : null,
            tipoExercicio == CALISTENIA ? this.getUltimaSerie(destaque) : null,
            variacao.isPadrao()
        );
    }

    private void migrarRegistrosPadraoSeNecessario(Exercicio exercicio, Integer usuarioId) {
        var strategy = this.getStrategyByTipoExercicio(exercicio.getTipoExercicio());
        if (strategy.contarRegistrosSemVariacao(exercicio.getId()) == 0) {
            return;
        }
        var variacaoPadrao = exercicioMapper.mapToExercicioVariacao(exercicio, usuarioId, null, "Padrão");
        variacaoPadrao.setPadrao(true);
        repository.saveAndFlush(variacaoPadrao);
        historicoService.salvar(variacaoPadrao, usuarioId, CADASTRO);
        strategy.migrarRegistrosSemVariacao(exercicio.getId(), variacaoPadrao.getId());
    }

    private String getUltimoValor(RegistroAtividadeResponse response,
                                  Function<RegistroAtividadeResponse, String> extrator) {
        return response == null ? null : extrator.apply(response);
    }

    private String getUltimaSerie(RegistroAtividadeResponse response) {
        if (response == null || response.ultimaQtdMaxRepeticoes() == null) {
            return null;
        }
        return response.ultimaQtdMaxRepeticoes() + " reps";
    }

    private TipoVariacaoResponse mapTipoVariacaoResponse(TipoVariacao tipoVariacao) {
        if (tipoVariacao == null) {
            return null;
        }
        return new TipoVariacaoResponse(tipoVariacao.getId(), tipoVariacao.getNome(), tipoVariacao.getDataCadastro());
    }

    private IRegistroAtividadeStrategy getStrategyByTipoExercicio(ETipoExercicio tipoExercicio) {
        return applicationContext.getBean(tipoExercicio.getServiceClass());
    }

    private void saveComHistorico(ExercicioVariacao exercicioVariacao, Integer usuarioId, EAcao acao) {
        repository.save(exercicioVariacao);
        historicoService.salvar(exercicioVariacao, usuarioId, acao);
    }

    private String getNomeVariacao(String exercicioNome, String tipoVariacaoNome) {
        return String.format("%s - %s", exercicioNome, tipoVariacaoNome);
    }

    private void aplicarValidacoes(ExercicioVariacaoRequest request, Exercicio exercicio) {
        request.aplicarGroupValidators(exercicio.getTipoExercicio());
        this.validarExercicioPermitidoParaTerVariacoes(exercicio);
        this.validarExercicioComMesmoTipoDeVariacao(exercicio.getId(), request.tipoVariacaoId());
        this.validarVariacaoComMesmoNome(request.nome());
    }

    private void validarExercicioPermitidoParaTerVariacoes(Exercicio exercicio) {
        if (Boolean.FALSE.equals(exercicio.getPossuiVariacao())) {
            throw new ValidacaoException("Este exercício não está permitido para ter variações.");
        }
    }

    private void validarExercicioComMesmoTipoDeVariacao(Integer exercicioId, Integer tipoVariacaoId) {
        if (tipoVariacaoId != null && repository.existsByTipoVariacao_IdAndExercicio_Id(tipoVariacaoId, exercicioId)) {
            throw new ValidacaoException(MSG_ERRO_VARIACAO_EXISTENTE);
        }
    }

    private void validarVariacaoComMesmoNome(String nome) {
        if (isNotBlank(nome) && repository.existsByNomeIgnoreCase(nome)) {
            throw new ValidacaoException(MSG_ERRO_VARIACAO_EXISTENTE);
        }
    }

    private ExercicioVariacao findCompleteById(Integer id) {
        return repository.findCompleteById(id)
            .orElseThrow(() -> new NotFoundException("Variação de exercício não encontrada."));
    }
}
