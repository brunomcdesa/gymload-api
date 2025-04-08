package br.com.gymloadapi.modulos.treino.service;

import br.com.gymloadapi.modulos.comum.enums.EAcao;
import br.com.gymloadapi.modulos.treino.mapper.TreinoMapper;
import br.com.gymloadapi.modulos.treino.model.Treino;
import br.com.gymloadapi.modulos.treino.repository.TreinoHistoricoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TreinoHistoricoService {

    private final TreinoMapper treinoMapper;
    private final TreinoHistoricoRepository repository;

    public void salvar(Treino treino, UUID usuarioId, EAcao acao) {
        repository.save(treinoMapper.mapToHistorico(treino, usuarioId, acao));
    }

}
