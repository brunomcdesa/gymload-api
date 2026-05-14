package br.com.gymloadapi.modulos.treino.service;

import br.com.gymloadapi.modulos.comum.enums.EAcao;
import br.com.gymloadapi.modulos.treino.mapper.TreinoSessaoMapper;
import br.com.gymloadapi.modulos.treino.model.TreinoSessao;
import br.com.gymloadapi.modulos.treino.repository.TreinoSessaoHistoricoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TreinoSessaoHistoricoService {

    private final TreinoSessaoMapper treinoSessaoMapper;
    private final TreinoSessaoHistoricoRepository repository;

    public void salvar(TreinoSessao treinoSessao, Integer usuarioId, EAcao acao) {
        repository.save(treinoSessaoMapper.mapToHistorico(treinoSessao, usuarioId, acao));
    }
}
