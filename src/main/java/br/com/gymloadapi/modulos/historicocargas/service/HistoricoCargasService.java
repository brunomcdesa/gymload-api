package br.com.gymloadapi.modulos.historicocargas.service;

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
import java.util.List;

import static java.util.Comparator.comparing;

@Service
@RequiredArgsConstructor
public class HistoricoCargasService {

    private final ExercicioService exercicioService;
    private final HistoricoCargasRepository repository;
    private final HistoricoCargasMapper historicoCargasMapper;

    public void salvar(HistoricoCargasRequest request, Usuario usuario) {
        var exercicio = exercicioService.findById(request.exercicioId());
        repository.save(historicoCargasMapper.mapToModel(request, exercicio, usuario));
    }

    public CargaResponse buscarUltimoHistoricoCargas(Integer exercicioId, Integer usuarioId) {
        var historicoCargas = this.getAllByExercicioId(exercicioId, usuarioId);

        return new CargaResponse(
            this.getMaiorCargaDoHistorico(historicoCargas),
            this.getHistoricoUltimoDia(historicoCargas).stream()
                .map(historicoCargasMapper::mapToResponse)
                .toList()
        );
    }

    public List<HistoricoCargasResponse> buscarHistoricoCargasCompleto(Integer exercicioId, Integer usuarioId) {
        return this.getAllByExercicioId(exercicioId, usuarioId).stream()
            .sorted(comparing(HistoricoCargas::getDataCadastro).reversed())
            .map(historicoCargasMapper::mapToResponse)
            .toList();
    }

    private List<HistoricoCargas> getAllByExercicioId(Integer exercicioId, Integer usuarioId) {
        return repository.findAllByExercicioIdAndUsuarioId(exercicioId, usuarioId);
    }

    private Double getMaiorCargaDoHistorico(List<HistoricoCargas> historicoCargas) {
        var optionalMaiorCarga = historicoCargas.stream()
            .map(HistoricoCargas::getCarga)
            .mapToDouble(Double::doubleValue)
            .max();

        return optionalMaiorCarga.isPresent()
            ? optionalMaiorCarga.getAsDouble()
            : null;
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
