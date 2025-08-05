package br.com.gymloadapi.modulos.registroatividade.service;

import br.com.gymloadapi.modulos.comum.enums.ETipoExercicio;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
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
import java.util.*;

@Service
@RequiredArgsConstructor
public class RegistroAtividadeService {

    private final ExercicioService exercicioService;
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
        request.aplicarGroupValidators(exercicio.getTipoExercicio().getGroupValidator());

        this.getStrategyByTipoExercicio(exercicio.getTipoExercicio())
            .salvarRegistro(request, exercicio, usuario);
    }

    @Transactional(readOnly = true)
    public List<RegistroAtividadeResponse> buscarDestaques(RegistroAtividadeFiltros filtros, Integer usuarioId) {
        var exercicios = exercicioService.findByIdIn(filtros.exerciciosIds());
        var responses = new ArrayList<RegistroAtividadeResponse>();

        exercicios.forEach(exercicio -> {
            var response = this.getStrategyByTipoExercicio(exercicio.getTipoExercicio())
                .buscarDestaque(exercicio.getId(), usuarioId);
            responses.add(response);
        });

        return responses;
    }

    public List<HistoricoRegistroAtividadeResponse> buscarRegistroAtividadeCompleto(Integer exercicioId, Integer usuarioId) {
        var exercicio = this.findExercicioById(exercicioId);

        return this.getStrategyByTipoExercicio(exercicio.getTipoExercicio())
            .buscarHistoricoRegistroCompleto(exercicioId, usuarioId);
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

    private Exercicio findExercicioById(Integer exercicioId) {
        return exercicioService.findById(exercicioId);
    }

    private IRegistroAtividadeStrategy getStrategyByTipoExercicio(ETipoExercicio tipoExercicio) {
        return this.strategies.get(tipoExercicio);
    }
}
