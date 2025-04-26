package br.com.gymloadapi.modulos.treino.service;

import br.com.gymloadapi.modulos.comum.enums.EAcao;
import br.com.gymloadapi.modulos.comum.enums.ESituacao;
import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.treino.dto.TreinoRequest;
import br.com.gymloadapi.modulos.treino.dto.TreinoResponse;
import br.com.gymloadapi.modulos.treino.mapper.TreinoMapper;
import br.com.gymloadapi.modulos.treino.model.Treino;
import br.com.gymloadapi.modulos.treino.repository.TreinoRepository;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.gymloadapi.modulos.comum.enums.EAcao.*;
import static br.com.gymloadapi.modulos.comum.enums.ESituacao.ATIVO;
import static br.com.gymloadapi.modulos.comum.enums.ESituacao.INATIVO;

@Service
@RequiredArgsConstructor
public class TreinoService {

    private final TreinoMapper treinoMapper;
    private final TreinoRepository repository;
    private final ExercicioService exercicioService;
    private final TreinoHistoricoService historicoService;

    public void salvar(TreinoRequest request, Usuario usuario) {
        var exercicios = exercicioService.findByIdIn(request.exerciciosIds());
        var treino = treinoMapper.mapToModel(request, usuario, exercicios);

        this.saveComHistorico(treino, usuario.getId(), CADASTRO);
    }

    public List<TreinoResponse> listarTodosDoUsuario(Integer usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream()
            .map(treinoMapper::mapToResponse)
            .toList();
    }

    public void editar(Integer id, TreinoRequest request, Integer usuarioId) {
        var treino = this.findCompleteById(id);
        if (!treino.getExerciciosIds().equals(request.exerciciosIds())) {
            var exercicios = exercicioService.findByIdIn(request.exerciciosIds());
            treino.alterarDados(request.nome(), exercicios);

            this.saveComHistorico(treino, usuarioId, EDICAO);
        }
    }

    public void ativar(Integer id, Integer usuarioId) {
        var treino = this.findCompleteById(id);
        this.validarSituacao(treino.getSituacao(), ATIVO);
        treino.alterarSituacao();

        this.saveComHistorico(treino, usuarioId, ATIVACAO);
    }

    public void inativar(Integer id, Integer usuarioId) {
        var treino = this.findCompleteById(id);
        this.validarSituacao(treino.getSituacao(), INATIVO);
        treino.alterarSituacao();

        this.saveComHistorico(treino, usuarioId, INATIVACAO);
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
}
