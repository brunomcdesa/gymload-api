package br.com.gymloadapi.modulos.exercicio.service;

import br.com.gymloadapi.modulos.comum.enums.EAcao;
import br.com.gymloadapi.modulos.exercicio.mapper.ExercicioMapper;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.exercicio.repository.ExercicioHistoricoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExercicioHistoricoService {

    private final ExercicioMapper exercicioMapper;
    private final ExercicioHistoricoRepository repository;

    public void salvar(Exercicio exercicio, Integer usuarioId, EAcao acao) {
        repository.save(exercicioMapper.mapToHistorico(exercicio, usuarioId, acao));
    }
}
