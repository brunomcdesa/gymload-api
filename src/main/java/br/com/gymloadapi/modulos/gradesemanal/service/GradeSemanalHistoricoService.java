package br.com.gymloadapi.modulos.gradesemanal.service;

import br.com.gymloadapi.modulos.comum.enums.EAcao;
import br.com.gymloadapi.modulos.gradesemanal.mapper.GradeSemanalMapper;
import br.com.gymloadapi.modulos.gradesemanal.model.GradeSemanal;
import br.com.gymloadapi.modulos.gradesemanal.repository.GradeSemanalHistoricoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GradeSemanalHistoricoService {

    private final GradeSemanalMapper mapper;
    private final GradeSemanalHistoricoRepository repository;

    public void salvar(GradeSemanal gradeSemanal, Integer usuarioId, EAcao acao) {
        repository.save(mapper.mapToHistorico(gradeSemanal, usuarioId, acao));
    }
}
