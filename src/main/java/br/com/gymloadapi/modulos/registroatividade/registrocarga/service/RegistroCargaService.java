package br.com.gymloadapi.modulos.registroatividade.registrocarga.service;

import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.registroatividade.dto.HistoricoRegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeRequest;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.factory.RegistroAtividadeFactory;
import br.com.gymloadapi.modulos.registroatividade.mapper.RegistroAtividadeMapper;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.model.RegistroCarga;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.repository.RegistroCargaRepository;
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
public class RegistroCargaService implements RegistroAtividadeFactory {

    private final RegistroCargaRepository repository;
    private final RegistroAtividadeMapper registroAtividadeMapper;

    @Override
    public void salvarRegistro(RegistroAtividadeRequest request, Exercicio exercicio, Usuario usuario) {
        repository.save(registroAtividadeMapper.mapToRegistroCarga(request, exercicio, usuario));
    }

    @Override
    public RegistroAtividadeResponse buscarDestaque(Integer exercicioId, Integer usuarioId) {
        var registrosCargas = this.getAllByExercicioId(exercicioId, usuarioId);

        return new RegistroAtividadeResponse(exercicioId, this.getDestaqueDoRegistro(registrosCargas),
            this.getUltimoRegistro(registrosCargas), null
        );
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

    private String getDestaqueDoRegistro(List<RegistroCarga> cargas) {
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
            : "-";
    }

    private String getUltimoRegistro(List<RegistroCarga> cargas) {
        var utimoRegistroId = cargas.stream()
            .map(RegistroCarga::getId)
            .max(Comparator.naturalOrder())
            .orElse(null);

        return cargas.stream()
            .filter(registro -> Objects.equals(registro.getId(), utimoRegistroId))
            .findFirst()
            .map(RegistroCarga::getCargaComUnidadePeso)
            .orElse("-");
    }

    private RegistroCarga findById(Integer id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Registro de carga não encontrado."));
    }
}
