package br.com.gymloadapi.modulos.historicocargas.service;

import br.com.gymloadapi.autenticacao.service.AutenticacaoService;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.historicocargas.dto.CargaResponse;
import br.com.gymloadapi.modulos.historicocargas.dto.HistoricoCargasRequest;
import br.com.gymloadapi.modulos.historicocargas.dto.HistoricoCargasResponse;
import br.com.gymloadapi.modulos.historicocargas.mapper.HistoricoCargasMapper;
import br.com.gymloadapi.modulos.historicocargas.model.HistoricoCargas;
import br.com.gymloadapi.modulos.historicocargas.repository.HistoricoCargasRepository;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
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

    public CargaResponse buscarUltimoHistoricoCargas(Integer exercicioId) {
        var historicoCargas = this.getAllByExercicioId(exercicioId);

        return new CargaResponse(
            this.getMaiorCargaDoHistorico(historicoCargas),
            this.getHistoricoUltimoDia(historicoCargas).stream()
                .map(historicoCargasMapper::mapToResponse)
                .toList()
        );
    }

    public List<HistoricoCargasResponse> buscarHistoricoCargasCompleto(Integer exercicioId) {
        return this.getAllByExercicioId(exercicioId).stream()
            .sorted(Comparator.comparing(HistoricoCargas::getDataCadastro).reversed())
            .map(historicoCargasMapper::mapToResponse)
            .toList();
    }

    private List<HistoricoCargas> getAllByExercicioId(Integer exercicioId) {
        return repository.findAllByExercicioIdAndUsuario_Id(exercicioId, this.getUsuarioAutenticado().getId());
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

    private List<HistoricoCargas> getHistoricoUltimoDia(List<HistoricoCargas> historicoCargas) {
        var ultimoDia = historicoCargas.stream()
            .map(HistoricoCargas::getDataCadastro)
            .max(LocalDate::compareTo)
            .orElse(LocalDate.now());

        return historicoCargas.stream()
            .filter(historico -> historico.getDataCadastro().equals(ultimoDia))
            .toList();
    }
}
