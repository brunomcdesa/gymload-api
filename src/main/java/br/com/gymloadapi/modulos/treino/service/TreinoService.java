package br.com.gymloadapi.modulos.treino.service;

import br.com.gymloadapi.modulos.comum.enums.EAcao;
import br.com.gymloadapi.modulos.comum.enums.ESituacao;
import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.treino.dto.TreinoRequest;
import br.com.gymloadapi.modulos.treino.dto.TreinoResponse;
import br.com.gymloadapi.modulos.treino.mapper.TreinoMapper;
import br.com.gymloadapi.modulos.treino.model.Treino;
import br.com.gymloadapi.modulos.treino.repository.TreinoRepository;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.gymloadapi.modulos.cache.utils.CacheUtils.CACHE_TODOS_TREINOS_ATIVOS_DO_USUARIO;
import static br.com.gymloadapi.modulos.cache.utils.CacheUtils.CACHE_TODOS_TREINOS_DO_USUARIO;
import static br.com.gymloadapi.modulos.comum.enums.EAcao.*;
import static br.com.gymloadapi.modulos.comum.enums.ESituacao.ATIVO;
import static br.com.gymloadapi.modulos.comum.enums.ESituacao.INATIVO;

@Service
@RequiredArgsConstructor
public class TreinoService {

    private final TreinoMapper treinoMapper;
    private final TreinoRepository repository;
    private final ExercicioService exercicioService;
    private final TreinoHistoricoService historicoService;

    @Caching(evict = {
        @CacheEvict(value = CACHE_TODOS_TREINOS_DO_USUARIO, key = "#usuario.id"),
        @CacheEvict(value = CACHE_TODOS_TREINOS_ATIVOS_DO_USUARIO, key = "#usuario.id")
    })
    public void salvar(TreinoRequest request, Usuario usuario) {
        var exercicios = exercicioService.findByIdIn(request.exerciciosIds());
        var treino = treinoMapper.mapToModel(request, usuario, exercicios);

        this.saveComHistorico(treino, usuario.getId(), CADASTRO);
    }

    @Cacheable(value = CACHE_TODOS_TREINOS_ATIVOS_DO_USUARIO, key = "#usuarioId")
    public List<TreinoResponse> listarTodosAtivosDoUsuario(Integer usuarioId) {
        return this.findAllByUsuarioIdAndSituacoes(usuarioId, List.of(ATIVO)).stream()
            .map(treinoMapper::mapToResponse)
            .toList();
    }

    @Cacheable(value = CACHE_TODOS_TREINOS_DO_USUARIO, key = "#usuarioId")
    public List<TreinoResponse> listarTodosDoUsuario(Integer usuarioId) {
        return this.findAllByUsuarioIdAndSituacoes(usuarioId, List.of(ATIVO, INATIVO)).stream()
            .map(treinoMapper::mapToResponse)
            .toList();
    }

    @Caching(evict = {
        @CacheEvict(value = CACHE_TODOS_TREINOS_DO_USUARIO, key = "#usuarioId"),
        @CacheEvict(value = CACHE_TODOS_TREINOS_ATIVOS_DO_USUARIO, key = "#usuarioId")
    })
    public void editar(Integer id, TreinoRequest request, Integer usuarioId) {
        var treino = this.findCompleteById(id);
        if (!treino.getExerciciosIds().equals(request.exerciciosIds()) || !treino.getNome().equals(request.nome())) {
            var exercicios = exercicioService.findByIdIn(request.exerciciosIds());
            treino.alterarDados(request.nome(), exercicios);

            this.saveComHistorico(treino, usuarioId, EDICAO);
        }
    }

    @Caching(evict = {
        @CacheEvict(value = CACHE_TODOS_TREINOS_DO_USUARIO, key = "#usuarioId"),
        @CacheEvict(value = CACHE_TODOS_TREINOS_ATIVOS_DO_USUARIO, key = "#usuarioId")
    })
    public void ativar(Integer id, Integer usuarioId) {
        var treino = this.findCompleteById(id);
        this.validarSituacao(treino.getSituacao(), ATIVO);
        treino.alterarSituacao();

        this.saveComHistorico(treino, usuarioId, ATIVACAO);
    }

    @Caching(evict = {
        @CacheEvict(value = CACHE_TODOS_TREINOS_DO_USUARIO, key = "#usuarioId"),
        @CacheEvict(value = CACHE_TODOS_TREINOS_ATIVOS_DO_USUARIO, key = "#usuarioId")
    })
    public void inativar(Integer id, Integer usuarioId) {
        var treino = this.findCompleteById(id);
        this.validarSituacao(treino.getSituacao(), INATIVO);
        treino.alterarSituacao();

        this.saveComHistorico(treino, usuarioId, INATIVACAO);
    }

    private Treino findCompleteById(Integer id) {
        return repository.findCompleteById(id)
            .orElseThrow(() -> new NotFoundException("Treino não encontrado."));
    }

    private void validarSituacao(ESituacao situacaoAtual, ESituacao situacaoDestino) {
        if (situacaoAtual == situacaoDestino) {
            throw new ValidacaoException("O treino já está na situacão " + situacaoDestino);
        }
    }

    private void saveComHistorico(Treino treino, Integer usuarioId, EAcao acao) {
        repository.save(treino);
        historicoService.salvar(treino, usuarioId, acao);
    }

    private List<Treino> findAllByUsuarioIdAndSituacoes(Integer usuarioId, List<ESituacao> situacoes) {
        return repository.findByUsuarioIdAndSituacaoIn(usuarioId, situacoes);
    }
}
