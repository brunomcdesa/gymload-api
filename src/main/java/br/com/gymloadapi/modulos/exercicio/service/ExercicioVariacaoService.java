package br.com.gymloadapi.modulos.exercicio.service;

import br.com.gymloadapi.modulos.comum.enums.EAcao;
import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioVariacaoRequest;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioVariacaoResponse;
import br.com.gymloadapi.modulos.exercicio.mapper.ExercicioMapper;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.exercicio.model.ExercicioVariacao;
import br.com.gymloadapi.modulos.exercicio.repository.ExercicioVariacaoRepository;
import br.com.gymloadapi.modulos.tipovariacao.service.TipoVariacaoService;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.gymloadapi.modulos.cache.utils.CacheUtils.CACHE_EXERCICIOS_VARIACOES_POR_EXERCICIO_ID;
import static br.com.gymloadapi.modulos.comum.enums.EAcao.CADASTRO;
import static br.com.gymloadapi.modulos.comum.enums.EAcao.EDICAO;
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

    @Caching(evict = {
        @CacheEvict(value = CACHE_EXERCICIOS_VARIACOES_POR_EXERCICIO_ID, allEntries = true)
    })
    public void salvar(ExercicioVariacaoRequest request, Integer usuarioAutenticadoId) {
        var exercicio = exercicioService.findById(request.exercicioBaseId());
        this.aplicarValidacoes(request, exercicio);
        var tipoVariacao = mapNull(request.tipoVariacaoId(), tipoVariacaoService::buscarPorId);
        var nomeVariacao = mapNullWithBackup(tipoVariacao,
            _tipoVariacao -> this.getNomeVariacao(exercicio.getNome(), _tipoVariacao.getNome()),
            request.nome());

        var variacao = exercicioMapper.mapToExercicioVariacao(exercicio, usuarioAutenticadoId,
            tipoVariacao, nomeVariacao);

        this.saveComHistorico(variacao, usuarioAutenticadoId, CADASTRO);
    }

    @Cacheable(value = CACHE_EXERCICIOS_VARIACOES_POR_EXERCICIO_ID, key = "#exercicioId")
    public List<ExercicioVariacaoResponse> buscarVariacoesDoExercicio(Integer exercicioId) {
        return repository.findAllByExercicioId(exercicioId).stream()
            .map(exercicioMapper::mapToExercicioVariacaoResponse)
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
