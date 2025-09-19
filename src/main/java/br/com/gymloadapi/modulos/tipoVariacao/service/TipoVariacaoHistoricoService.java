package br.com.gymloadapi.modulos.tipoVariacao.service;

import br.com.gymloadapi.modulos.comum.enums.EAcao;
import br.com.gymloadapi.modulos.tipoVariacao.mapper.TipoVariacaoMapper;
import br.com.gymloadapi.modulos.tipoVariacao.model.TipoVariacao;
import br.com.gymloadapi.modulos.tipoVariacao.repository.TipoVariacaoHistoricoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TipoVariacaoHistoricoService {

    private final TipoVariacaoMapper tipoVariacaoMapper;
    private final TipoVariacaoHistoricoRepository repository;

    public void salvar(TipoVariacao tipoVariacao, Integer usuarioId, EAcao acao) {
        repository.save(tipoVariacaoMapper.mapToHistorico(tipoVariacao, usuarioId, acao));
    }
}
