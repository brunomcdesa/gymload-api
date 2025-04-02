package br.com.gymloadapi.modulos.historicocargas.service;

import br.com.gymloadapi.autenticacao.service.AutenticacaoService;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.historicocargas.dto.CargaResponse;
import br.com.gymloadapi.modulos.historicocargas.dto.HistoricoCargasRequest;
import br.com.gymloadapi.modulos.historicocargas.mapper.HistoricoCargasMapper;
import br.com.gymloadapi.modulos.historicocargas.model.HistoricoCargas;
import br.com.gymloadapi.modulos.historicocargas.repository.HistoricoCargasRepository;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoricoCargasService {

    private final ExercicioService exercicioService;
    private final HistoricoCargasRepository repository;
    private final AutenticacaoService autenticacaoService;
    private final HistoricoCargasMapper historicoCargasMapper;

    public void salvar(HistoricoCargasRequest request) {
        var usuarioAutenticado = this.getUsuarioAutenticado();
        var exercicio = exercicioService.findById(request.exercicioId());

        repository.save(historicoCargasMapper.mapToModel(request, exercicio, usuarioAutenticado));
    }

    public CargaResponse buscarExerciciosDoUsuario(Integer exercicioId) {
        var usuarioAutenticadoId = this.getUsuarioAutenticado().getId();
        var historicoCargas = repository.findAllByExercicioIdAndUsuario_Id(exercicioId, usuarioAutenticadoId);

        return new CargaResponse(
            this.getMaiorCargaDoHistorico(historicoCargas),
            historicoCargas.stream()
                .map(historicoCargasMapper::mapToResponse)
                .toList()
        );
    }

    private Usuario getUsuarioAutenticado() {
        return autenticacaoService.getUsuarioAutenticado();
    }

    private Double getMaiorCargaDoHistorico(List<HistoricoCargas> historicoCargas) {
        return historicoCargas.stream()
            .map(HistoricoCargas::getCarga)
            .mapToDouble(Double::doubleValue)
            .max()
            .orElse(0.0);
    }
}
