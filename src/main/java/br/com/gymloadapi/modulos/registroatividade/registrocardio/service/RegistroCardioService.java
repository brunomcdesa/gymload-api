package br.com.gymloadapi.modulos.registroatividade.registrocardio.service;

import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.registroatividade.dto.HistoricoRegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeRequest;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.factory.RegistroAtividadeFactory;
import br.com.gymloadapi.modulos.registroatividade.mapper.RegistroAtividadeMapper;
import br.com.gymloadapi.modulos.registroatividade.registrocardio.model.RegistroCardio;
import br.com.gymloadapi.modulos.registroatividade.registrocardio.repository.RegistroCardioRepository;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static br.com.gymloadapi.modulos.comum.utils.ValidacaoUtils.validarUsuarioAlteracao;
import static java.util.Comparator.comparing;

@Service
@RequiredArgsConstructor
public class RegistroCardioService implements RegistroAtividadeFactory {

    private final RegistroCardioRepository repository;
    private final RegistroAtividadeMapper registroAtividadeMapper;

    @Override
    public void salvarRegistro(RegistroAtividadeRequest request, Exercicio exercicio, Usuario usuario) {
        var registroCardio = registroAtividadeMapper.mapToRegistroCardio(request, exercicio, usuario);
        repository.save(registroCardio);
    }

    @Override
    public RegistroAtividadeResponse buscarDestaque(Integer exercicioId, Integer usuarioId) {
        var registroCardio = this.getAllByExercicioId(exercicioId, usuarioId);

        return new RegistroAtividadeResponse(exercicioId, this.getDestaqueDoHistorico(registroCardio),
            null, this.getUltimoResgistro(registroCardio)
        );
    }

    @Override
    public List<HistoricoRegistroAtividadeResponse> buscarHistoricoRegistroCompleto(Integer exercicioId, Integer usuarioId) {
        return this.getAllByExercicioId(exercicioId, usuarioId).stream()
            .sorted(comparing(RegistroCardio::getDataCadastro).reversed())
            .map(registroAtividadeMapper::mapToHistoricoRegistroAtividadeAerobicoResponse)
            .toList();
    }

    @Override
    public void editarRegistro(Integer registroAtividadeId, RegistroAtividadeRequest request, Usuario usuario) {
        var registroCardio = this.findById(registroAtividadeId);
        validarUsuarioAlteracao(registroCardio.getUsuarioId(), usuario, "alterar as informações deste registro de cardio");
        registroAtividadeMapper.editarRegistroCardio(request, registroCardio);
        repository.save(registroCardio);
    }

    private List<RegistroCardio> getAllByExercicioId(Integer exercicioId, Integer usuarioId) {
        return repository.findAllByExercicioIdAndUsuarioId(exercicioId, usuarioId);
    }

    private RegistroCardio findById(Integer id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Registro de Cardio não encontrado."));
    }

    private String getDestaqueDoHistorico(List<RegistroCardio> registros) {
        var optionalMaiorDistancia = registros.stream()
            .map(RegistroCardio::getDistancia)
            .mapToDouble(Double::doubleValue)
            .max();

        return optionalMaiorDistancia.isPresent()
            ? registros.stream()
            .filter(carga -> carga.getDistancia() == optionalMaiorDistancia.getAsDouble())
            .map(RegistroCardio::getDistanciaFormatada)
            .findFirst()
            .orElse("-")
            : "-";
    }

    private String getUltimoResgistro(List<RegistroCardio> registros) {
        var ultimoRegistro = registros.stream()
            .map(RegistroCardio::getId)
            .max(Comparator.naturalOrder())
            .orElse(null);

        return registros.stream()
            .filter(registro -> Objects.equals(registro.getId(), ultimoRegistro))
            .map(RegistroCardio::getDistanciaFormatada)
            .findFirst()
            .orElse("-");
    }
}
