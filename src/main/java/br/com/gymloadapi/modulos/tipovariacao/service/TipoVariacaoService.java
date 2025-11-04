package br.com.gymloadapi.modulos.tipovariacao.service;

import br.com.gymloadapi.modulos.comum.dto.SelectResponse;
import br.com.gymloadapi.modulos.comum.enums.EAcao;
import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.tipovariacao.dto.TipoVariacaoRequest;
import br.com.gymloadapi.modulos.tipovariacao.dto.TipoVariacaoResponse;
import br.com.gymloadapi.modulos.tipovariacao.mapper.TipoVariacaoMapper;
import br.com.gymloadapi.modulos.tipovariacao.model.TipoVariacao;
import br.com.gymloadapi.modulos.tipovariacao.repository.TipoVariacaoRepository;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.gymloadapi.modulos.cache.utils.CacheUtils.*;
import static br.com.gymloadapi.modulos.comum.enums.EAcao.CADASTRO;
import static br.com.gymloadapi.modulos.comum.enums.EAcao.EDICAO;

@Service
@RequiredArgsConstructor
public class TipoVariacaoService {

    private final TipoVariacaoRepository repository;
    private final TipoVariacaoMapper tipoVariacaoMapper;
    private final TipoVariacaoHistoricoService historicoService;

    @Caching(evict = {
        @CacheEvict(value = CACHE_TODOS_TIPOS_VARIACOES, allEntries = true),
        @CacheEvict(value = CACHE_TODOS_TIPOS_VARIACOES_SELECT, allEntries = true),
    })
    public void salvar(TipoVariacaoRequest request, Usuario usuarioAutenticado) {
        this.validarTipoVariacaoMesmoNome(request.nome());
        var tipoVariacao = tipoVariacaoMapper.mapToModel(request, usuarioAutenticado);

        this.saveComHistorico(tipoVariacao, usuarioAutenticado, CADASTRO);
    }

    @Cacheable(value = CACHE_TODOS_TIPOS_VARIACOES)
    public List<TipoVariacaoResponse> buscarTodos() {
        return repository.findAll().stream()
            .map(tipoVariacaoMapper::mapToResponse)
            .toList();
    }

    @Caching(evict = {
        @CacheEvict(value = CACHE_TODOS_TIPOS_VARIACOES, allEntries = true),
        @CacheEvict(value = CACHE_TODOS_TIPOS_VARIACOES_SELECT, allEntries = true),
    })
    public void editar(Integer id, TipoVariacaoRequest request, Usuario usuarioAutenticado) {
        this.validarTipoVariacaoMesmoNome(request.nome());
        var tipoVariacao = this.findById(id);

        tipoVariacaoMapper.editarTipoVariacao(request, tipoVariacao);

        this.saveComHistorico(tipoVariacao, usuarioAutenticado, EDICAO);
    }

    @Cacheable(value = CACHE_TODOS_TIPOS_VARIACOES_SELECT)
    public List<SelectResponse> getSelect() {
        return repository.findAll().stream()
            .map(tipoVariacaoMapper::mapToSelectResponse)
            .toList();
    }

    private void validarTipoVariacaoMesmoNome(String nome) {
        if (repository.existsByNomeIgnoreCase(nome)) {
            throw new ValidacaoException("Já existe uma variação com este nome.");
        }
    }

    private void saveComHistorico(TipoVariacao tipoVariacao, Usuario usuarioAutenticado, EAcao acao) {
        repository.save(tipoVariacao);
        historicoService.salvar(tipoVariacao, usuarioAutenticado.getId(), acao);
    }

    private TipoVariacao findById(Integer id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Tipo de Variação não encontrado."));
    }
}
