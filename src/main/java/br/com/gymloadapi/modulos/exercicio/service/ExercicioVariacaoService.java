package br.com.gymloadapi.modulos.exercicio.service;

import br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioVariacaoRequest;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioVariacaoResponse;
import br.com.gymloadapi.modulos.exercicio.mapper.ExercicioMapper;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.exercicio.repository.ExercicioVariacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class ExercicioVariacaoService {

    private static final String MSG_ERRO_VARIACAO_EXISTENTE = "Já existe uma variação igual para este exercício.";

    private final ExercicioMapper exercicioMapper;
    private final ExercicioService exercicioService;
    private final ExercicioVariacaoRepository repository;

    public void salvar(ExercicioVariacaoRequest request, Integer usuarioAutenticadoId) {
        var exercicio = exercicioService.findById(request.exercicioBaseId());
        this.aplicarValidacoes(request, exercicio);
        var nomeVariacao = exercicio.isExercicioMusculacao()
            ? this.getNomeVariacao(exercicio.getNome(), request.tipoEquipamento().getDescricao())
            : request.nome();

        var variacao = exercicioMapper.mapToExercicioVariacao(exercicio, usuarioAutenticadoId,
            request.tipoEquipamento(), nomeVariacao);

        repository.save(variacao);
    }

    public List<ExercicioVariacaoResponse> buscarVariacoesDoExercicio(Integer exercicioId) {
        return repository.findAllByExercicio_Id(exercicioId).stream()
            .map(exercicioMapper::mapToExercicioVariacaoResponse)
            .toList();
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
}
