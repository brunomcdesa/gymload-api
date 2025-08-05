package br.com.gymloadapi.modulos.registroatividade.registroaerobico.service;

import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.registroatividade.dto.HistoricoRegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeRequest;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.strategy.IRegistroAtividadeStrategy;
import br.com.gymloadapi.modulos.registroatividade.mapper.RegistroAtividadeMapper;
import br.com.gymloadapi.modulos.registroatividade.registroaerobico.model.RegistroAerobico;
import br.com.gymloadapi.modulos.registroatividade.registroaerobico.repository.RegistroAerobicoRepository;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static br.com.gymloadapi.modulos.comum.utils.ValidacaoUtils.validarUsuarioAlteracao;
import static java.lang.String.format;
import static java.util.Comparator.comparing;

@Service
@RequiredArgsConstructor
public class RegistroAerobicoService implements IRegistroAtividadeStrategy {

    private final RegistroAerobicoRepository repository;
    private final RegistroAtividadeMapper registroAtividadeMapper;

    @Override
    public void salvarRegistro(RegistroAtividadeRequest request, Exercicio exercicio, Usuario usuario) {
        var registroAerobico = registroAtividadeMapper.mapToRegistroAerobico(request, exercicio, usuario);
        repository.save(registroAerobico);
    }

    @Override
    public RegistroAtividadeResponse buscarDestaque(Integer exercicioId, Integer usuarioId) {
        var registrosAerobico = this.getAllByExercicioId(exercicioId, usuarioId);
        var destaqueRegistro = this.getDestaqueDoRegistro(registrosAerobico);
        var ultimoRegistro = this.getUltimoResgistro(registrosAerobico);

        return registroAtividadeMapper.mapToRegistroAtividadeResponse(exercicioId, destaqueRegistro , null,
            ultimoRegistro, null);
    }

    @Override
    public List<HistoricoRegistroAtividadeResponse> buscarHistoricoRegistroCompleto(Integer exercicioId, Integer usuarioId) {
        return this.getAllByExercicioId(exercicioId, usuarioId).stream()
            .sorted(comparing(RegistroAerobico::getDataCadastro).reversed())
            .map(registroAtividadeMapper::mapToHistoricoRegistroAtividadeAerobicoResponse)
            .toList();
    }

    @Override
    public void editarRegistro(Integer registroAtividadeId, RegistroAtividadeRequest request, Usuario usuario) {
        var registroAerobico = this.findById(registroAtividadeId);
        validarUsuarioAlteracao(registroAerobico.getUsuarioId(), usuario, "alterar as informações deste registro de aeróbico");
        registroAtividadeMapper.editarRegistroAerobico(request, registroAerobico);
        repository.save(registroAerobico);
    }

    @Override
    public void excluirRegistro(Integer registroAtividadeId, Usuario usuario) {
        var registroAerobico = this.findById(registroAtividadeId);
        validarUsuarioAlteracao(registroAerobico.getUsuarioId(), usuario, "excluir este registro de aeróbico");

        repository.delete(registroAerobico);
    }

    @Override
    public void repetirUltimoRegistro(Exercicio exercicio, Usuario usuario) {
        repository.findLastByExercicioIdAndUsuarioId(exercicio.getId(), usuario.getId())
            .ifPresentOrElse(registroAerobico -> {
                var novoRegistroAerobico = registroAtividadeMapper.copiarRegistroAerobico(registroAerobico);
                repository.save(novoRegistroAerobico);
            }, () -> {
                throw new NotFoundException(format("Você ainda não possui nenhum registro para o exercício aeróbico %s.",
                    exercicio.getNome()));
            });
    }

    @Override
    public void repetirRegistro(Integer registroId) {
        var registroAerobico = this.findById(registroId);

        var novoRegistroAerobico = registroAtividadeMapper.copiarRegistroAerobico(registroAerobico);
        repository.save(novoRegistroAerobico);
    }

    private List<RegistroAerobico> getAllByExercicioId(Integer exercicioId, Integer usuarioId) {
        return repository.findAllByExercicioIdAndUsuarioId(exercicioId, usuarioId);
    }

    private RegistroAerobico findById(Integer id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Registro de aeróbico não encontrado."));
    }

    private String getDestaqueDoRegistro(List<RegistroAerobico> registrosAerobico) {
        var optionalMaiorDistancia = registrosAerobico.stream()
            .map(RegistroAerobico::getDistancia)
            .mapToDouble(Double::doubleValue)
            .max();

        return optionalMaiorDistancia.isPresent()
            ? registrosAerobico.stream()
            .filter(registro -> registro.getDistancia() == optionalMaiorDistancia.getAsDouble())
            .map(RegistroAerobico::getDistanciaFormatada)
            .findFirst()
            .orElse("-")
            : "-";
    }

    private String getUltimoResgistro(List<RegistroAerobico> registrosAerobico) {
        var ultimoRegistro = registrosAerobico.stream()
            .map(RegistroAerobico::getId)
            .max(Comparator.naturalOrder())
            .orElse(null);

        return registrosAerobico.stream()
            .filter(registro -> Objects.equals(registro.getId(), ultimoRegistro))
            .map(RegistroAerobico::getDistanciaFormatada)
            .findFirst()
            .orElse("-");
    }
}
