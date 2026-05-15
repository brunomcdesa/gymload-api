package br.com.gymloadapi.modulos.treino.service;

import br.com.gymloadapi.modulos.comum.enums.EAcao;
import br.com.gymloadapi.modulos.comum.enums.ESituacao;
import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.exercicio.mapper.ExercicioMapper;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.treino.dto.TreinoCompartilhadoResponse;
import br.com.gymloadapi.modulos.treino.dto.TreinoCompartilhamentoTokenResponse;
import br.com.gymloadapi.modulos.treino.dto.TreinoImportarRequest;
import br.com.gymloadapi.modulos.treino.dto.TreinoRequest;
import br.com.gymloadapi.modulos.treino.dto.TreinoResponse;
import br.com.gymloadapi.modulos.treino.mapper.TreinoMapper;
import br.com.gymloadapi.modulos.treino.model.Treino;
import br.com.gymloadapi.modulos.treino.model.TreinoCompartilhamento;
import br.com.gymloadapi.modulos.treino.repository.TreinoCompartilhamentoRepository;
import br.com.gymloadapi.modulos.treino.repository.TreinoRepository;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static br.com.gymloadapi.modulos.cache.utils.CacheUtils.CACHE_TREINOS_DO_USUARIO_POR_SITUACAO;
import static br.com.gymloadapi.modulos.comum.enums.EAcao.*;
import static br.com.gymloadapi.modulos.comum.enums.ESituacao.ATIVO;
import static br.com.gymloadapi.modulos.comum.enums.ESituacao.INATIVO;

@Service
@RequiredArgsConstructor
public class TreinoService {

    private static final String CHARS_CODIGO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int TAMANHO_CODIGO = 8;
    private static final int DIAS_VALIDADE_COMPARTILHAMENTO = 30;

    private final TreinoMapper treinoMapper;
    private final TreinoRepository repository;
    private final ExercicioService exercicioService;
    private final ExercicioMapper exercicioMapper;
    private final TreinoHistoricoService historicoService;
    private final TreinoCompartilhamentoRepository compartilhamentoRepository;

    @CacheEvict(value = CACHE_TREINOS_DO_USUARIO_POR_SITUACAO, allEntries = true)
    public void salvar(TreinoRequest request, Usuario usuario) {
        var exercicios = exercicioService.findByIdIn(request.exerciciosIds());
        var treino = treinoMapper.mapToModel(request, usuario, exercicios);

        this.saveComHistorico(treino, usuario.getId(), CADASTRO);
    }

    @Cacheable(value = CACHE_TREINOS_DO_USUARIO_POR_SITUACAO,
        key = "#usuarioId + '_' + #situacao.name() + '_' + #buscarImportados")
    public List<TreinoResponse> listarPorSituacao(Integer usuarioId, ESituacao situacao,
                                                   boolean buscarImportados) {
        var treinos = this.findAllByUsuarioIdAndSituacoes(usuarioId, List.of(situacao));
        if (buscarImportados) {
            treinos = treinos.stream()
                .filter(t -> Boolean.TRUE.equals(t.getImportado()))
                .toList();
        }
        return treinos.stream()
            .map(treinoMapper::mapToResponse)
            .toList();
    }

    @CacheEvict(value = CACHE_TREINOS_DO_USUARIO_POR_SITUACAO, allEntries = true)
    public void editar(Integer id, TreinoRequest request, Integer usuarioId) {
        var treino = this.findCompleteById(id);
        if (!treino.getExerciciosIds().equals(request.exerciciosIds()) || !treino.getNome().equals(request.nome())) {
            var exercicios = exercicioService.findByIdIn(request.exerciciosIds());
            treino.alterarDados(request.nome(), exercicios);

            this.saveComHistorico(treino, usuarioId, EDICAO);
        }
    }

    @CacheEvict(value = CACHE_TREINOS_DO_USUARIO_POR_SITUACAO, allEntries = true)
    public void ativar(Integer id, Integer usuarioId) {
        var treino = this.findCompleteById(id);
        this.validarSituacao(treino.getSituacao(), ATIVO);
        treino.alterarSituacao();

        this.saveComHistorico(treino, usuarioId, ATIVACAO);
    }

    @CacheEvict(value = CACHE_TREINOS_DO_USUARIO_POR_SITUACAO, allEntries = true)
    public void inativar(Integer id, Integer usuarioId) {
        var treino = this.findCompleteById(id);
        this.validarSituacao(treino.getSituacao(), INATIVO);
        treino.alterarSituacao();

        this.saveComHistorico(treino, usuarioId, INATIVACAO);
    }

    @Transactional
    @CacheEvict(value = CACHE_TREINOS_DO_USUARIO_POR_SITUACAO, allEntries = true)
    public TreinoCompartilhamentoTokenResponse compartilhar(Integer treinoId, Integer usuarioId) {
        var treino = this.findByIdAndUsuarioId(treinoId, usuarioId);

        if (treino.getSituacao() == INATIVO) {
            throw new ValidacaoException("Não é possível compartilhar um treino inativo.");
        }

        var idsSerializados = this.serializarIds(treino.getExerciciosIds());
        return compartilhamentoRepository
            .findByUsuarioIdAndNomeTreinoAndExerciciosIdsAndDataExpiracaoAfter(
                usuarioId, treino.getNome(), idsSerializados, LocalDateTime.now())
            .map(comp -> new TreinoCompartilhamentoTokenResponse(
                comp.getCodigo(), comp.getDataExpiracao().toLocalDate()))
            .orElseGet(() -> this.criarCompartilhamento(treino, idsSerializados));
    }

    private TreinoCompartilhamentoTokenResponse criarCompartilhamento(Treino treino, String idsSerializados) {
        var dataExpiracao = LocalDateTime.now().plusDays(DIAS_VALIDADE_COMPARTILHAMENTO);
        var compartilhamento = TreinoCompartilhamento.builder()
            .token(UUID.randomUUID().toString())
            .codigo(this.gerarCodigoCurto())
            .nomeTreino(treino.getNome())
            .exerciciosIds(idsSerializados)
            .dataCriacao(LocalDateTime.now())
            .dataExpiracao(dataExpiracao)
            .usuario(treino.getUsuario())
            .build();

        compartilhamentoRepository.save(compartilhamento);
        return new TreinoCompartilhamentoTokenResponse(
            compartilhamento.getCodigo(), dataExpiracao.toLocalDate());
    }

    public TreinoCompartilhadoResponse buscarCompartilhado(String codigo) {
        var comp = compartilhamentoRepository.findByCodigo(codigo)
            .orElseThrow(() -> new NotFoundException("Compartilhamento não encontrado."));

        if (comp.getDataExpiracao().isBefore(LocalDateTime.now())) {
            throw new ValidacaoException("Este código expirou.");
        }

        var exercicios = exercicioService.findByIdIn(this.deserializarIds(comp.getExerciciosIds()))
            .stream()
            .map(exercicioMapper::mapModelToResponse)
            .toList();

        return treinoMapper.mapToCompartilhadoResponse(comp, exercicios);
    }

    @Transactional
    @CacheEvict(value = CACHE_TREINOS_DO_USUARIO_POR_SITUACAO, allEntries = true)
    public void importar(TreinoImportarRequest request, Usuario usuario) {
        var comp = compartilhamentoRepository.findByCodigo(request.codigo())
            .orElseThrow(() -> new NotFoundException("Compartilhamento não encontrado."));

        if (comp.getDataExpiracao().isBefore(LocalDateTime.now())) {
            throw new ValidacaoException("Este código expirou.");
        }

        var exercicios = exercicioService.findByIdIn(this.deserializarIds(comp.getExerciciosIds()));
        var treino = treinoMapper.mapCompartilhadoToModel(request.nome(), usuario, exercicios);
        this.saveComHistorico(treino, usuario.getId(), CADASTRO);
    }

    public Treino findByIdAndUsuarioId(Integer id, Integer usuarioId) {
        return repository.findCompleteByIdAndUsuarioId(id, usuarioId)
            .orElseThrow(() -> new NotFoundException("Treino não encontrado."));
    }

    private Treino findCompleteById(Integer id) {
        return repository.findCompleteById(id)
            .orElseThrow(() -> new NotFoundException("Treino não encontrado."));
    }

    private void validarSituacao(ESituacao situacaoAtual, ESituacao situacaoDestino) {
        if (situacaoAtual == situacaoDestino) {
            throw new ValidacaoException("O treino já está na situacão " + situacaoDestino);
        }
    }

    private void saveComHistorico(Treino treino, Integer usuarioId, EAcao acao) {
        repository.save(treino);
        historicoService.salvar(treino, usuarioId, acao);
    }

    private List<Treino> findAllByUsuarioIdAndSituacoes(Integer usuarioId, List<ESituacao> situacoes) {
        return repository.findByUsuarioIdAndSituacaoIn(usuarioId, situacoes);
    }

    private String gerarCodigoCurto() {
        var random = new SecureRandom();
        return IntStream.range(0, TAMANHO_CODIGO)
            .mapToObj(i -> String.valueOf(CHARS_CODIGO.charAt(random.nextInt(CHARS_CODIGO.length()))))
            .collect(Collectors.joining());
    }

    private String serializarIds(List<Integer> ids) {
        return ids.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(","));
    }

    private List<Integer> deserializarIds(String csv) {
        return Arrays.stream(csv.split(","))
            .map(Integer::parseInt)
            .toList();
    }
}
