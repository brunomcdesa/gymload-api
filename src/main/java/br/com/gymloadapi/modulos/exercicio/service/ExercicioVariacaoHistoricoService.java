package br.com.gymloadapi.modulos.exercicio.service;

import br.com.gymloadapi.modulos.comum.enums.EAcao;
import br.com.gymloadapi.modulos.exercicio.mapper.ExercicioMapper;
import br.com.gymloadapi.modulos.exercicio.model.ExercicioVariacao;
import br.com.gymloadapi.modulos.exercicio.repository.ExercicioVariacaoHistoricoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExercicioVariacaoHistoricoService {

    private final ExercicioMapper exercicioMapper;
    private final ExercicioVariacaoHistoricoRepository repository;

    public void salvar(ExercicioVariacao exercicioVariacao, Integer usuarioId, EAcao acao) {
        repository.save(exercicioMapper.mapToVariacaoHistorico(exercicioVariacao, usuarioId, acao));
    }
}
