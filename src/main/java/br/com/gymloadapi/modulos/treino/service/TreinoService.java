package br.com.gymloadapi.modulos.treino.service;

import br.com.gymloadapi.autenticacao.service.AutenticacaoService;
import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.treino.dto.TreinoRequest;
import br.com.gymloadapi.modulos.treino.dto.TreinoResponse;
import br.com.gymloadapi.modulos.treino.mapper.TreinoMapper;
import br.com.gymloadapi.modulos.treino.model.Treino;
import br.com.gymloadapi.modulos.treino.repository.TreinoRepository;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TreinoService {

    private final TreinoMapper treinoMapper;
    private final TreinoRepository repository;
    private final ExercicioService exercicioService;
    private final AutenticacaoService autenticacaoService;

    public void salvar(TreinoRequest request) {
        var usuario = this.getUsuarioAutenticado();
        var exercicios = exercicioService.findByIdIn(request.exerciciosIds());

        repository.save(treinoMapper.mapToModel(request, usuario, exercicios));
    }

    public List<TreinoResponse> listarTodosDoUsuario() {
        var usuario = this.getUsuarioAutenticado();
        return repository.findByUsuarioId(usuario.getId()).stream()
            .map(treinoMapper::mapToResponse)
            .toList();
    }

    public void editar(Integer id, TreinoRequest request) {
        var treino = this.findCompleteById(id);
        if (!treino.getExerciciosIds().equals(request.exerciciosIds())) {
            var exercicios = exercicioService.findByIdIn(request.exerciciosIds());
            treino.setExercicios(exercicios);

            repository.save(treino);
        }
    }

    private Usuario getUsuarioAutenticado() {
        return autenticacaoService.getUsuarioAutenticado();
    }

    private Treino findCompleteById(Integer id) {
        return repository.findCompleteById(id)
            .orElseThrow(() -> new NotFoundException("Treino não encontrado."));
    }
}
