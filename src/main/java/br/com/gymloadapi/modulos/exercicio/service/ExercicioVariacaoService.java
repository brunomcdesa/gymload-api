package br.com.gymloadapi.modulos.exercicio.service;

import br.com.gymloadapi.modulos.comum.enums.EAcao;
import br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento;
import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioVariacaoRequest;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioVariacaoResponse;
import br.com.gymloadapi.modulos.exercicio.mapper.ExercicioMapper;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.exercicio.model.ExercicioVariacao;
import br.com.gymloadapi.modulos.exercicio.repository.ExercicioVariacaoRepository;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.gymloadapi.modulos.comum.enums.EAcao.CADASTRO;
import static br.com.gymloadapi.modulos.comum.enums.EAcao.EDICAO;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class ExercicioVariacaoService {

    private static final String MSG_ERRO_VARIACAO_EXISTENTE = "Já existe uma variação igual para este exercício.";

    private final ExercicioMapper exercicioMapper;
    private final ExercicioService exercicioService;
    private final ExercicioVariacaoRepository repository;
    private final ExercicioVariacaoHistoricoService historicoService;

    public void salvar(ExercicioVariacaoRequest request, Integer usuarioAutenticadoId) {
        var exercicio = exercicioService.findById(request.exercicioBaseId());
        this.aplicarValidacoes(request, exercicio);
        var nomeVariacao = exercicio.isExercicioMusculacao()
            ? this.getNomeVariacao(exercicio.getNome(), request.tipoEquipamento().getDescricao())
            : request.nome();

        var variacao = exercicioMapper.mapToExercicioVariacao(exercicio, usuarioAutenticadoId,
            request.tipoEquipamento(), nomeVariacao);

        this.saveComHistorico(variacao, usuarioAutenticadoId, CADASTRO);
    }

    public List<ExercicioVariacaoResponse> buscarVariacoesDoExercicio(Integer exercicioId) {
        return repository.findAllByExercicio_Id(exercicioId).stream()
            .map(exercicioMapper::mapToExercicioVariacaoResponse)
            .toList();
    }

    public void editarVariacao(Integer id, ExercicioVariacaoRequest request,
                               Usuario usuarioAutenticado) {
        var variacao = this.findById(id);
        this.aplicarValidacoes(request, variacao.getExercicio());
        var nomeVariacao = variacao.isVariacaoDeExercicioMusculacao()
            ? this.getNomeVariacao(variacao.getExercicioNome(), request.tipoEquipamento().getDescricao())
            : request.nome();

        exercicioMapper.editarVariacao(request.tipoEquipamento(), nomeVariacao, variacao);

        this.saveComHistorico(variacao, usuarioAutenticado.getId(), EDICAO);
    }

    private void saveComHistorico(ExercicioVariacao exercicioVariacao, Integer usuarioId, EAcao acao) {
        repository.save(exercicioVariacao);
        historicoService.salvar(exercicioVariacao, usuarioId, acao);
    }

    private String getNomeVariacao(String exercicioNome, String tipoEquipamentoDescricao) {
        return String.format("%s - %s", exercicioNome, tipoEquipamentoDescricao);
    }

    private void aplicarValidacoes(ExercicioVariacaoRequest request, Exercicio exercicio) {
        request.aplicarGroupValidators(exercicio.getTipoExercicio());
        this.validarExercicioPermitidoParaTerVariacoes(exercicio);
        this.validarVariacaoComMesmoTipoDeEquipamento(exercicio.getId(), request.tipoEquipamento());
        this.validarVariacaoComMesmoNome(request.nome());
    }

    private void validarExercicioPermitidoParaTerVariacoes(Exercicio exercicio) {
        if (Boolean.FALSE.equals(exercicio.getPossuiVariacao())) {
            throw new ValidacaoException("Este exercício não está permitido para ter variações.");
        }
    }

    private void validarVariacaoComMesmoTipoDeEquipamento(Integer exercicioId, ETipoEquipamento tipoEquipamento) {
        if (tipoEquipamento != null && repository.existsByTipoEquipamentoAndExercicio_Id(tipoEquipamento, exercicioId)) {
            throw new ValidacaoException(MSG_ERRO_VARIACAO_EXISTENTE);
        }
    }

    private void validarVariacaoComMesmoNome(String nome) {
        if (isNotBlank(nome) && repository.existsByNomeIgnoreCase(nome)) {
            throw new ValidacaoException(MSG_ERRO_VARIACAO_EXISTENTE);
        }
    }

    private ExercicioVariacao findById(Integer id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Variação de exercício não encontrada."));
    }
}
