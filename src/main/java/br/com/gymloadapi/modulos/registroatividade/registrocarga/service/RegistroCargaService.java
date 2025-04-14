package br.com.gymloadapi.modulos.registroatividade.registrocarga.service;

import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.registroatividade.dto.HistoricoRegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeRequest;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.factory.RegistroAtividadeFactory;
import br.com.gymloadapi.modulos.registroatividade.mapper.RegistroAtividadeMapper;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.dto.CargaResponse;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.dto.HistoricoCargasRequest;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.dto.HistoricoCargasResponse;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.mapper.HistoricoCargasMapper;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.model.RegistroCarga;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.repository.RegistroCargaRepository;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static br.com.gymloadapi.modulos.comum.utils.ValidacaoUtils.validarUsuarioAlteracao;
import static java.util.Comparator.comparing;

@Service
@RequiredArgsConstructor
public class RegistroCargaService implements RegistroAtividadeFactory {

    private final ExercicioService exercicioService;
    private final RegistroCargaRepository repository;
    private final HistoricoCargasMapper historicoCargasMapper;
    private final RegistroAtividadeMapper registroAtividadeMapper;

    public void salvar(HistoricoCargasRequest request, Usuario usuario) {
        var exercicio = exercicioService.findById(request.exercicioId());
        repository.save(historicoCargasMapper.mapToModel(request, exercicio, usuario));
    }

    @Override
    public void salvarRegistro(RegistroAtividadeRequest request, Exercicio exercicio, Usuario usuario) {
        repository.save(registroAtividadeMapper.mapToRegistroCarga(request, exercicio, usuario));
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

    @Override
    public RegistroAtividadeResponse buscarUltimoRegistro(Integer exercicioId, Integer usuarioId) {
        var registroCarga = this.getAllByExercicioId(exercicioId, usuarioId);

        return new RegistroAtividadeResponse(
            this.getDestaqueDoHistorico(registroCarga),
            this.getHistoricoUltimoDia(registroCarga).stream()
                .map(registroAtividadeMapper::mapToHistoricoRegistroAtividadeMusculacaoResponse)
                .toList()
        );
    }

    public List<HistoricoCargasResponse> buscarHistoricoCargasCompleto(Integer exercicioId, Integer usuarioId) {
        return this.getAllByExercicioId(exercicioId, usuarioId).stream()
            .sorted(comparing(RegistroCarga::getDataCadastro).reversed())
            .map(historicoCargasMapper::mapToResponse)
            .toList();
    }

    @Override
    public List<HistoricoRegistroAtividadeResponse> buscarHistoricoRegistroCompleto(Integer exercicioId, Integer usuarioId) {
        return this.getAllByExercicioId(exercicioId, usuarioId).stream()
            .sorted(comparing(RegistroCarga::getDataCadastro).reversed())
            .map(registroAtividadeMapper::mapToHistoricoRegistroAtividadeMusculacaoResponse)
            .toList();
    }

    @Override
    public void editarRegistro(Integer registroAtividadeId, RegistroAtividadeRequest request, Usuario usuario) {
        var registroCarga = this.findById(registroAtividadeId);
        validarUsuarioAlteracao(registroCarga.getUsuarioId(), usuario, "alterar as informações deste registro de carga");
        registroAtividadeMapper.editarRegistroCarga(request, registroCarga);
        repository.save(registroCarga);
    }

    private List<RegistroCarga> getAllByExercicioId(Integer exercicioId, Integer usuarioId) {
        return repository.findAllByExercicioIdAndUsuarioId(exercicioId, usuarioId);
    }

    private Double getMaiorCargaDoHistorico(List<RegistroCarga> cargases) {
        var optionalMaiorCarga = cargases.stream()
            .map(RegistroCarga::getPeso)
            .mapToDouble(Double::doubleValue)
            .max();

        return optionalMaiorCarga.isPresent()
            ? optionalMaiorCarga.getAsDouble()
            : null;
    }

    private String getDestaqueDoHistorico(List<RegistroCarga> cargas) {
        var optionalMaiorCarga = cargas.stream()
            .map(RegistroCarga::getPeso)
            .mapToDouble(Double::doubleValue)
            .max();

        return optionalMaiorCarga.isPresent()
            ? cargas.stream()
            .filter(carga -> carga.getPeso() == optionalMaiorCarga.getAsDouble())
            .map(RegistroCarga::getCargaComUnidadePeso)
            .findFirst()
            .orElse(null)
            : null;
    }

    private List<RegistroCarga> getHistoricoUltimoDia(List<RegistroCarga> cargas) {
        var ultimoDia = cargas.stream()
            .map(RegistroCarga::getDataCadastro)
            .max(LocalDate::compareTo)
            .orElse(LocalDate.now());

        return cargas.stream()
            .filter(historico -> historico.getDataCadastro().equals(ultimoDia))
            .toList();
    }

    private RegistroCarga findById(Integer id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Registro de carga não encontrado."));
    }
}
