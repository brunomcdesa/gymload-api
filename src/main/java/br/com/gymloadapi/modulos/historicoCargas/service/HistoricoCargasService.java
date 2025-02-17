package br.com.gymloadapi.modulos.historicoCargas.service;

import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.historicoCargas.dto.HistoricoCargasRequest;
import br.com.gymloadapi.modulos.historicoCargas.dto.HistoricoCargasResponse;
import br.com.gymloadapi.modulos.historicoCargas.mapper.HistoricoCargasMapper;
import br.com.gymloadapi.modulos.historicoCargas.model.HistoricoCargas;
import br.com.gymloadapi.modulos.historicoCargas.repository.HistoricoCargasRepository;
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

    public List<HistoricoCargasResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::mapToResponse)
                .toList();
    }
}
