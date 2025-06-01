package br.com.gymloadapi.modulos.registroatividade.registromusculacao.service;

import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.registroatividade.dto.HistoricoRegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeRequest;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.strategy.IRegistroAtividadeStrategy;
import br.com.gymloadapi.modulos.registroatividade.mapper.RegistroAtividadeMapper;
import br.com.gymloadapi.modulos.registroatividade.registromusculacao.model.RegistroMusculacao;
import br.com.gymloadapi.modulos.registroatividade.registromusculacao.repository.RegistroMusculacaoRepository;
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
public class RegistroMusculacaoService implements IRegistroAtividadeStrategy {

    private final RegistroMusculacaoRepository repository;
    private final RegistroAtividadeMapper registroAtividadeMapper;

    @Override
    public void salvarRegistro(RegistroAtividadeRequest request, Exercicio exercicio, Usuario usuario) {
        repository.save(registroAtividadeMapper.mapToRegistroMusculacao(request, exercicio, usuario));
    }

    @Override
    public RegistroAtividadeResponse buscarDestaque(Integer exercicioId, Integer usuarioId) {
        var registrosMusculacao = this.getAllByExercicioId(exercicioId, usuarioId);
        var destaqueRegistro = this.getDestaqueDoRegistro(registrosMusculacao);
        var ultimoRegistro = this.getUltimoRegistro(registrosMusculacao);

        return registroAtividadeMapper.mapToRegistroAtividadeResponse(exercicioId, destaqueRegistro, ultimoRegistro,
            null, null);
    }

    @Override
    public List<HistoricoRegistroAtividadeResponse> buscarHistoricoRegistroCompleto(Integer exercicioId, Integer usuarioId) {
        return this.getAllByExercicioId(exercicioId, usuarioId).stream()
            .sorted(comparing(RegistroMusculacao::getDataCadastro).reversed())
            .map(registroAtividadeMapper::mapToHistoricoRegistroAtividadeMusculacaoResponse)
            .toList();
    }

    @Override
    public void editarRegistro(Integer registroAtividadeId, RegistroAtividadeRequest request, Usuario usuario) {
        var registroMusculacao = this.findById(registroAtividadeId);
        validarUsuarioAlteracao(registroMusculacao.getUsuarioId(), usuario,
            "alterar as informações deste registro de musculação");
        registroAtividadeMapper.editarRegistroMusculacao(request, registroMusculacao);
        repository.save(registroMusculacao);
    }

    @Override
    public void excluirRegistro(Integer registroAtividadeId, Usuario usuario) {
        var registroMusculacao = this.findById(registroAtividadeId);
        validarUsuarioAlteracao(registroMusculacao.getUsuarioId(), usuario, "excluir este registro de musculação");

        repository.delete(registroMusculacao);
    }

    @Override
    public void repetirUltimoRegistro(Exercicio exercicio, Usuario usuario) {
        repository.findLastByExercicioIdAndUsuarioId(exercicio.getId(), usuario.getId())
            .ifPresentOrElse(registroMusculacao -> {
                var novoRegistroMusculacao = registroAtividadeMapper.copiarRegistroMusculacao(registroMusculacao);
                repository.save(novoRegistroMusculacao);
            }, () -> {
                throw new NotFoundException(format("Você ainda não possui nenhum registro para o exercício de musculação %s.",
                    exercicio.getNome()));
            });
    }

    private List<RegistroMusculacao> getAllByExercicioId(Integer exercicioId, Integer usuarioId) {
        return repository.findAllByExercicioIdAndUsuarioId(exercicioId, usuarioId);
    }

    private String getDestaqueDoRegistro(List<RegistroMusculacao> registrosMusculacao) {
        var optionalMaiorPeso = registrosMusculacao.stream()
            .map(RegistroMusculacao::getPeso)
            .mapToDouble(Double::doubleValue)
            .max();

        return optionalMaiorPeso.isPresent()
            ? registrosMusculacao.stream()
            .filter(registro -> registro.getPeso() == optionalMaiorPeso.getAsDouble())
            .map(RegistroMusculacao::getPesoComUnidadePeso)
            .findFirst()
            .orElse(null)
            : "-";
    }

    private String getUltimoRegistro(List<RegistroMusculacao> registrosMusculacao) {
        var utimoRegistroId = registrosMusculacao.stream()
            .map(RegistroMusculacao::getId)
            .max(Comparator.naturalOrder())
            .orElse(null);

        return registrosMusculacao.stream()
            .filter(registro -> Objects.equals(registro.getId(), utimoRegistroId))
            .findFirst()
            .map(RegistroMusculacao::getPesoComUnidadePeso)
            .orElse("-");
    }

    private RegistroMusculacao findById(Integer id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Registro de musculação não encontrado."));
    }
}
