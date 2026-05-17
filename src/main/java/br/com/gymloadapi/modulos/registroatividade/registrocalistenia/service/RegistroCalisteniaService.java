package br.com.gymloadapi.modulos.registroatividade.registrocalistenia.service;

import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.dashboard.dto.RecordeRecenteResponse;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.exercicio.model.ExercicioVariacao;
import br.com.gymloadapi.modulos.registroatividade.dto.HistoricoRegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeRequest;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.mapper.RegistroAtividadeMapper;
import br.com.gymloadapi.modulos.registroatividade.registrocalistenia.model.RegistroCalistenia;
import br.com.gymloadapi.modulos.registroatividade.registrocalistenia.repository.RegistroCalisteniaRepository;
import br.com.gymloadapi.modulos.registroatividade.strategy.IRegistroAtividadeStrategy;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static br.com.gymloadapi.modulos.comum.utils.ValidacaoUtils.validarUsuarioAlteracao;
import static java.lang.String.format;
import static java.util.Comparator.comparing;

@Service
@RequiredArgsConstructor
public class RegistroCalisteniaService implements IRegistroAtividadeStrategy {

    private final RegistroCalisteniaRepository repository;
    private final RegistroAtividadeMapper registroAtividadeMapper;

    @Override
    public void salvarRegistro(RegistroAtividadeRequest request, Exercicio exercicio,
                               ExercicioVariacao exercicioVariacao, Usuario usuario) {
        request.validarPesoEUnidadePeso();
        repository.save(registroAtividadeMapper.mapToRegistroCalistenia(request, exercicio, exercicioVariacao, usuario));
    }

    @Override
    public RegistroAtividadeResponse buscarDestaque(Integer exercicioId, Integer usuarioId) {
        var registrosCalistenia = this.getAllByExercicioIdSemVariacao(exercicioId, usuarioId);
        return this.montarResponseDestaque(exercicioId, registrosCalistenia);
    }

    @Override
    public RegistroAtividadeResponse buscarDestaquePorVariacao(Integer exercicioId, Integer variacaoId,
                                                               Integer usuarioId) {
        var registrosCalistenia = repository.findAllByExercicioIdAndVariacaoIdAndUsuarioId(exercicioId, variacaoId,
            usuarioId);
        return this.montarResponseDestaque(exercicioId, registrosCalistenia);
    }

    private RegistroAtividadeResponse montarResponseDestaque(Integer exercicioId,
                                                             List<RegistroCalistenia> registrosCalistenia) {
        var destaqueRegistro = this.getDestaqueDoRegistro(registrosCalistenia);
        var ultimoRegistro = this.getUltimoRegistro(registrosCalistenia);

        return registroAtividadeMapper.mapToRegistroAtividadeResponse(exercicioId, destaqueRegistro, null,
            null, ultimoRegistro);
    }

    @Override
    public List<HistoricoRegistroAtividadeResponse> buscarHistoricoRegistroCompleto(Integer exercicioId,
                                                                                    Integer variacaoId,
                                                                                    Integer usuarioId) {
        var registros = variacaoId != null
            ? repository.findAllByExercicioIdAndVariacaoIdAndUsuarioId(exercicioId, variacaoId, usuarioId)
            : this.getAllByExercicioIdSemVariacao(exercicioId, usuarioId);

        return registros.stream()
            .sorted(comparing(RegistroCalistenia::getDataCadastro).reversed())
            .map(registroAtividadeMapper::mapToHistoricoRegistroAtividadeCalisteniaResponse)
            .toList();
    }

    @Override
    public void editarRegistro(Integer registroAtividadeId, RegistroAtividadeRequest request, Usuario usuario) {
        var registroCalistenia = this.findById(registroAtividadeId);
        validarUsuarioAlteracao(registroCalistenia.getUsuarioId(), usuario,
            "alterar as informações deste registro de calistenia");
        registroAtividadeMapper.editarRegistroCalistenia(request, registroCalistenia);
        repository.save(registroCalistenia);
    }

    @Override
    public void excluirRegistro(Integer registroAtividadeId, Usuario usuario) {
        var registroCalistenia = this.findById(registroAtividadeId);
        validarUsuarioAlteracao(registroCalistenia.getUsuarioId(), usuario, "excluir este registro de calistenia");

        repository.delete(registroCalistenia);
    }

    @Override
    public void repetirUltimoRegistro(Exercicio exercicio, Usuario usuario) {
        repository.findLastByExercicioIdAndUsuarioId(exercicio.getId(), usuario.getId())
            .ifPresentOrElse(registroCalistenia -> {
                var novoRegistroCalistenia = registroAtividadeMapper.copiarRegistroCalistenia(registroCalistenia);
                repository.save(novoRegistroCalistenia);
            }, () -> {
                throw new NotFoundException(format("Você ainda não possui nenhum registro para o exercício calistênico %s.",
                    exercicio.getNome()));
            });
    }

    @Override
    public void repetirRegistro(Integer registroId) {
        var registroCalistenia = this.findById(registroId);

        var novoRegistroCalistenia = registroAtividadeMapper.copiarRegistroCalistenia(registroCalistenia);
        repository.save(novoRegistroCalistenia);
    }

    @Override
    public boolean existeRegistroHojeParaExercicios(List<Integer> exercicioIds, Integer usuarioId, LocalDate hoje) {
        return repository.existeRegistroHojeParaExercicios(exercicioIds, usuarioId, hoje);
    }

    private List<RegistroCalistenia> getAllByExercicioIdSemVariacao(Integer exercicioId, Integer usuarioId) {
        return repository.findAllByExercicioIdAndUsuarioId(exercicioId, usuarioId);
    }

    private String getDestaqueDoRegistro(List<RegistroCalistenia> registrosCalistenia) {
        var optionalMaiorQtdRepeticoes = registrosCalistenia.stream()
            .map(RegistroCalistenia::getQtdRepeticoes)
            .mapToInt(Integer::intValue)
            .max();

        return optionalMaiorQtdRepeticoes.isPresent()
            ? registrosCalistenia.stream()
              .filter(registro -> registro.getQtdRepeticoes() == optionalMaiorQtdRepeticoes.getAsInt())
              .map(RegistroCalistenia::getQtdRepeticoesDestaque)
              .findFirst()
              .orElse(null)
            : "-";
    }

    private Integer getUltimoRegistro(List<RegistroCalistenia> registrosCalistenia) {
        var utimoRegistroId = registrosCalistenia.stream()
            .map(RegistroCalistenia::getId)
            .max(Comparator.naturalOrder())
            .orElse(null);

        return registrosCalistenia.stream()
            .filter(registro -> Objects.equals(registro.getId(), utimoRegistroId))
            .findFirst()
            .map(RegistroCalistenia::getQtdRepeticoes)
            .orElse(null);
    }

    @Override
    public void migrarRegistrosSemVariacao(Integer exercicioId, Integer variacaoPadraoId) {
        repository.migrarRegistrosSemVariacao(exercicioId, variacaoPadraoId);
    }

    @Override
    public void moverRegistros(List<Integer> registroIds, Integer variacaoDestinoId, Integer usuarioId) {
        repository.moverRegistros(registroIds, variacaoDestinoId, usuarioId);
    }

    @Override
    public long contarRegistrosSemVariacao(Integer exercicioId) {
        return repository.countByExercicio_IdAndExercicioVariacaoIsNull(exercicioId);
    }

    @Override
    public long contarRegistrosPorVariacao(Integer variacaoId) {
        return repository.countByExercicioVariacao_Id(variacaoId);
    }

    @Override
    public List<RecordeRecenteResponse> buscarRecordePessoalResume(Integer usuarioId) {
        var todos = repository.findTodosPrsPorUsuarioId(usuarioId);
        return todos.stream()
            .collect(Collectors.groupingBy(r -> r.getExercicio().getId()))
            .values().stream()
            .map(registros -> {
                var maiorReps = registros.stream()
                    .mapToInt(RegistroCalistenia::getQtdRepeticoes)
                    .max()
                    .orElse(0);
                return registros.stream()
                    .filter(r -> r.getQtdRepeticoes() == maiorReps)
                    .max(Comparator.comparing(RegistroCalistenia::getDataCadastro))
                    .map(r -> new RecordeRecenteResponse(
                        r.getExercicio().getId(),
                        r.getExercicio().getNome(),
                        "CALISTENIA",
                        r.getQtdRepeticoesDestaque(),
                        r.getDataCadastro()
                    ))
                    .orElse(null);
            })
            .filter(Objects::nonNull)
            .toList();
    }

    private RegistroCalistenia findById(Integer id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Registro de calistenia não encontrado."));
    }
}
