package br.com.gymloadapi.modulos.registroatividade.service;

import br.com.gymloadapi.modulos.comum.enums.ETipoExercicio;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.exercicio.model.ExercicioVariacao;
import br.com.gymloadapi.modulos.exercicio.repository.ExercicioVariacaoRepository;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.registroatividade.dto.HistoricoRegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeFiltros;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeRequest;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.strategy.IRegistroAtividadeStrategy;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RegistroAtividadeService {

    private final ExercicioService exercicioService;
    private final ExercicioVariacaoRepository exercicioVariacaoRepository;
    private final ApplicationContext applicationContext;
    private final Map<ETipoExercicio, IRegistroAtividadeStrategy> strategies = new EnumMap<>(ETipoExercicio.class);

    @PostConstruct
    public void init() {
        Arrays.stream(ETipoExercicio.values())
            .forEach(tipoExercicio ->
                this.strategies.put(tipoExercicio, applicationContext.getBean(tipoExercicio.getServiceClass())));
    }

    public void salvar(RegistroAtividadeRequest request, Usuario usuario) {
        var exercicio = this.findExercicioById(request.exercicioId());
        var exercicioVariacao = this.validarEBuscarVariacao(request, exercicio);
        request.aplicarGroupValidators(exercicio.getTipoExercicio().getGroupValidator());

        this.getStrategyByTipoExercicio(exercicio.getTipoExercicio())
            .salvarRegistro(request, exercicio, exercicioVariacao, usuario);
    }

    @Transactional(readOnly = true)
    public List<RegistroAtividadeResponse> buscarDestaques(RegistroAtividadeFiltros filtros, Integer usuarioId) {
        var exercicios = exercicioService.findByIdIn(filtros.exerciciosIds());
        var responses = new ArrayList<RegistroAtividadeResponse>();

        exercicios.forEach(exercicio -> {
            if (Boolean.TRUE.equals(exercicio.getPossuiVariacao())) {
                responses.add(new RegistroAtividadeResponse(exercicio.getId(), null, null, null, null));
            } else {
                var response = this.getStrategyByTipoExercicio(exercicio.getTipoExercicio())
                    .buscarDestaque(exercicio.getId(), usuarioId);
                responses.add(response);
            }
        });

        return responses;
    }

    public List<HistoricoRegistroAtividadeResponse> buscarRegistroAtividadeCompleto(Integer exercicioId,
                                                                                     Integer variacaoId,
                                                                                     Integer usuarioId) {
        var exercicio = this.findExercicioById(exercicioId);

        if (Boolean.TRUE.equals(exercicio.getPossuiVariacao()) && variacaoId == null) {
            return List.of();
        }

        return this.getStrategyByTipoExercicio(exercicio.getTipoExercicio())
            .buscarHistoricoRegistroCompleto(exercicioId, variacaoId, usuarioId);
    }

    public void editar(Integer registroAtividadeId, RegistroAtividadeRequest request, Usuario usuario) {
        var exercicio = this.findExercicioById(request.exercicioId());
        request.aplicarGroupValidators(exercicio.getTipoExercicio().getGroupValidator());

        this.getStrategyByTipoExercicio(exercicio.getTipoExercicio())
            .editarRegistro(registroAtividadeId, request, usuario);
    }

    public void excluir(Integer registroAtividadeId, Integer exercicioId, Usuario usuario) {
        var exercicio = this.findExercicioById(exercicioId);

        this.getStrategyByTipoExercicio(exercicio.getTipoExercicio())
            .excluirRegistro(registroAtividadeId, usuario);
    }

    public void repetirUltimoRegistro(Integer exercicioId, Usuario usuario) {
        var exercicio = this.findExercicioById(exercicioId);

        this.getStrategyByTipoExercicio(exercicio.getTipoExercicio())
            .repetirUltimoRegistro(exercicio, usuario);
    }

    public void repetirRegistro(Integer exercicioId, Integer registroId) {
        var exercicio = this.findExercicioById(exercicioId);

        this.getStrategyByTipoExercicio(exercicio.getTipoExercicio())
            .repetirRegistro(registroId);
    }

    public boolean existeRegistroHojeParaExercicios(List<Integer> exercicioIds, Integer usuarioId) {
        var hoje = LocalDate.now();
        return strategies.values().stream()
            .anyMatch(strategy -> strategy.existeRegistroHojeParaExercicios(exercicioIds, usuarioId, hoje));
    }

    private ExercicioVariacao validarEBuscarVariacao(RegistroAtividadeRequest request, Exercicio exercicio) {
        if (Boolean.TRUE.equals(exercicio.getPossuiVariacao())) {
            if (request.variacaoId() == null) {
                throw new ValidacaoException("Este exercício possui variações. Informe a variação para registrar a atividade.");
            }
            return exercicioVariacaoRepository.findByIdAndExercicio_Id(request.variacaoId(), exercicio.getId())
                .orElseThrow(() -> new ValidacaoException("A variação informada não pertence a este exercício."));
        }
        if (request.variacaoId() != null) {
            throw new ValidacaoException(
                "Este exercício não possui variações. Não informe uma variação ao registrar a atividade.");
        }
        return null;
    }

    private Exercicio findExercicioById(Integer exercicioId) {
        return exercicioService.findById(exercicioId);
    }

    private IRegistroAtividadeStrategy getStrategyByTipoExercicio(ETipoExercicio tipoExercicio) {
        return this.strategies.get(tipoExercicio);
    }
}
