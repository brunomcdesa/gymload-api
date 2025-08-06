package br.com.gymloadapi.modulos.exercicio.service;

import br.com.gymloadapi.modulos.exercicio.dto.ExercicioVariacaoRequest;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioVariacaoResponse;
import br.com.gymloadapi.modulos.exercicio.mapper.ExercicioMapper;
import br.com.gymloadapi.modulos.exercicio.repository.ExercicioVariacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExercicioVariacaoService {

    private final ExercicioMapper exercicioMapper;
    private final ExercicioService exercicioService;
    private final ExercicioVariacaoRepository repository;

    public void salvar(ExercicioVariacaoRequest request, Integer usuarioAutenticadoId) {
        var exercicio = exercicioService.findById(request.exercicioBaseId());
        var variacao = exercicioMapper.mapToExercicioVariacao(exercicio, usuarioAutenticadoId,
            request.tipoEquipamento(), this.getNomeVariacao(exercicio.getNome(), request.tipoEquipamento().getDescricao()));

        repository.save(variacao);
    }

    public List<ExercicioVariacaoResponse> buscarVariacoesDoExercicio(Integer exercicioId) {
        return repository.findAllByExercicio_Id(exercicioId).stream()
            .map(exercicioMapper::mapToExercicioVariacaoResponse)
            .toList();
    }

    private String getNomeVariacao(String exercicioNome, String tipoEquipamentoDescricao) {
        return String.format("%s - %s", exercicioNome, tipoEquipamentoDescricao);
    }
}
