package br.com.gymloadapi.modulos.exercicio.service;

import br.com.gymloadapi.autenticacao.service.AutenticacaoService;
import br.com.gymloadapi.modulos.comum.dto.SelectResponse;
import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioRequest;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioResponse;
import br.com.gymloadapi.modulos.exercicio.mapper.ExercicioMapper;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.exercicio.repository.ExercicioRepository;
import br.com.gymloadapi.modulos.grupomuscular.service.GrupoMuscularService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExercicioService {

    private final ExercicioRepository repository;
    private final ExercicioMapper exercicioMapper;
    private final GrupoMuscularService grupoMuscularService;
    private final AutenticacaoService autenticacaoService;

    public void salvar(ExercicioRequest request) {
        var grupoMuscular = grupoMuscularService.findById(request.grupoMuscularId());
        repository.save(exercicioMapper.mapToModel(request, grupoMuscular));
    }

    public List<ExercicioResponse> buscarTodos() {
        return this.findAll().stream()
            .map(exercicioMapper::mapModelToResponse)
            .toList();
    }

    public Exercicio findById(Integer id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Exercício não encontrado."));
    }

    public List<SelectResponse> buscarTodosSelect() {
        return this.findAll().stream()
            .map(exercicioMapper::mapToSelectResponse)
            .toList();
    }

    public List<Exercicio> findByIdIn(List<Integer> exercicioIds) {
        return repository.findByIdIn(exercicioIds);
    }

    public List<ExercicioResponse> buscarExerciciosPorTreino(Integer treinoId) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        return repository.buscarExerciciosPorTreinoAndUsuario(treinoId, usuarioAutenticado.getId()).stream()
            .map(exercicioMapper::mapModelToResponse)
            .toList();
    }

    private List<Exercicio> findAll() {
        return repository.findAll();
    }
}
