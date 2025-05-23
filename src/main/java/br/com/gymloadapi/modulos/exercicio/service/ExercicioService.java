package br.com.gymloadapi.modulos.exercicio.service;

import br.com.gymloadapi.modulos.comum.dto.SelectResponse;
import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioFiltro;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioRequest;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioResponse;
import br.com.gymloadapi.modulos.exercicio.mapper.ExercicioMapper;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.exercicio.repository.ExercicioRepository;
import br.com.gymloadapi.modulos.grupomuscular.service.GrupoMuscularService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.gymloadapi.modulos.comum.utils.MapUtils.mapNullBoolean;

@Service
@RequiredArgsConstructor
public class ExercicioService {

    private final ExercicioRepository repository;
    private final ExercicioMapper exercicioMapper;
    private final GrupoMuscularService grupoMuscularService;

    public void salvar(ExercicioRequest request) {
        request.aplicarGroupValidators();
        var exercicio = exercicioMapper.mapToModel(
            request,
            mapNullBoolean(request.isExercicioMusculacao(), () -> grupoMuscularService.findById(request.grupoMuscularId()))
        );

        repository.save(exercicio);
    }

    public List<ExercicioResponse> buscarTodos(ExercicioFiltro filtro) {
        return repository.findAllCompleteByPredicate(filtro.toPredicate()).stream()
            .map(exercicioMapper::mapModelToResponse)
            .toList();
    }

    public Exercicio findById(Integer id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Exercício não encontrado."));
    }

    public List<SelectResponse> buscarTodosSelect() {
        return repository.findAllComplete().stream()
            .map(exercicioMapper::mapToSelectResponse)
            .toList();
    }

    public List<Exercicio> findByIdIn(List<Integer> exercicioIds) {
        return repository.findByIdIn(exercicioIds);
    }

    public List<ExercicioResponse> buscarExerciciosPorTreino(Integer treinoId) {
        return repository.buscarExerciciosPorTreino(treinoId).stream()
            .map(exercicioMapper::mapModelToResponse)
            .toList();
    }
}
