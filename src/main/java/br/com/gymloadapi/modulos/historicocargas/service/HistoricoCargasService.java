package br.com.gymloadapi.modulos.historicocargas.service;

import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.historicocargas.dto.HistoricoCargasRequest;
import br.com.gymloadapi.modulos.historicocargas.dto.HistoricoCargasResponse;
import br.com.gymloadapi.modulos.historicocargas.mapper.HistoricoCargasMapper;
import br.com.gymloadapi.modulos.historicocargas.repository.HistoricoCargasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoricoCargasService {

    private final HistoricoCargasMapper mapper;
    private final ExercicioService exercicioService;
    private final HistoricoCargasRepository repository;

    public void salvar(HistoricoCargasRequest request) {
        var exercicio = exercicioService.findById(request.exercicioId());

        repository.save(mapper.mapToModel(request, exercicio));
    }

    public List<HistoricoCargasResponse> findByExercicioId(Integer exercicioId) {
        return repository.findAllByExercicioId(exercicioId).stream()
            .map(mapper::mapToResponse)
            .toList();
    }
}
