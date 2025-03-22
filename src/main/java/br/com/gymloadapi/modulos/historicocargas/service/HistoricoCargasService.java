package br.com.gymloadapi.modulos.historicocargas.service;

import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.historicocargas.dto.HistoricoCargasRequest;
import br.com.gymloadapi.modulos.historicocargas.dto.HistoricoCargasResponse;
import br.com.gymloadapi.modulos.historicocargas.mapper.HistoricoCargasMapper;
import br.com.gymloadapi.modulos.historicocargas.repository.HistoricoCargasRepository;
import br.com.gymloadapi.modulos.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoricoCargasService {

    private final HistoricoCargasMapper mapper;
    private final UsuarioService usuarioService;
    private final ExercicioService exercicioService;
    private final HistoricoCargasRepository repository;

    public void salvar(HistoricoCargasRequest request) {
        var exercicio = exercicioService.findById(request.exercicioId());
        var usuario = usuarioService.findById(request.usuarioId());

        repository.save(mapper.mapToModel(request, exercicio, usuario));
    }

    public List<HistoricoCargasResponse> buscarExerciciosDoUsuario(Integer exercicioId, UUID usuarioId) {
        return repository.findAllByExercicioIdAndUsuario_Id(exercicioId, usuarioId).stream()
            .map(mapper::mapToResponse)
            .toList();
    }
}
