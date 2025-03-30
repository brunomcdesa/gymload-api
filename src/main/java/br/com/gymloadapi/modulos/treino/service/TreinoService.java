package br.com.gymloadapi.modulos.treino.service;

import br.com.gymloadapi.autenticacao.service.AutenticacaoService;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.treino.dto.TreinoRequest;
import br.com.gymloadapi.modulos.treino.dto.TreinoResponse;
import br.com.gymloadapi.modulos.treino.mapper.TreinoMapper;
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

    private Usuario getUsuarioAutenticado() {
        return autenticacaoService.getUsuarioAutenticado();
    }
}
