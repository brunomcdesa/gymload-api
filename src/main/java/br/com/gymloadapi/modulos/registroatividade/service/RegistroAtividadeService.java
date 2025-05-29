package br.com.gymloadapi.modulos.registroatividade.service;

import br.com.gymloadapi.modulos.comum.service.LocatorService;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.registroatividade.dto.HistoricoRegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeFiltros;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeRequest;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeResponse;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistroAtividadeService {

    private final LocatorService locatorService;
    private final ExercicioService exercicioService;

    public void salvar(RegistroAtividadeRequest request, Usuario usuario) {
        var exercicio = this.findExercicioById(request.exercicioId());
        request.aplicarGroupValidators(exercicio.getTipoExercicio().getGroupValidator());

        locatorService.getRegistroAtividadeService(exercicio.getTipoExercicio())
            .salvarRegistro(request, exercicio, usuario);
    }

    @Transactional(readOnly = true)
    public List<RegistroAtividadeResponse> buscarDestaques(RegistroAtividadeFiltros filtros, Integer usuarioId) {
        var exercicios = exercicioService.findByIdIn(filtros.exerciciosIds());
        var responses = new ArrayList<RegistroAtividadeResponse>();

        exercicios.forEach(exercicio -> {
            var response = locatorService.getRegistroAtividadeService(exercicio.getTipoExercicio())
                .buscarDestaque(exercicio.getId(), usuarioId);
            responses.add(response);
        });

        return responses;
    }

    public List<HistoricoRegistroAtividadeResponse> buscarRegistroAtividadeCompleto(Integer exercicioId, Integer usuarioId) {
        var exercicio = this.findExercicioById(exercicioId);

        return locatorService.getRegistroAtividadeService(exercicio.getTipoExercicio())
            .buscarHistoricoRegistroCompleto(exercicioId, usuarioId);
    }

    public void editar(Integer registroAtividadeId, RegistroAtividadeRequest request, Usuario usuario) {
        var exercicio = this.findExercicioById(request.exercicioId());
        request.aplicarGroupValidators(exercicio.getTipoExercicio().getGroupValidator());

        locatorService.getRegistroAtividadeService(exercicio.getTipoExercicio())
            .editarRegistro(registroAtividadeId, request, usuario);
    }

    public void excluir(Integer registroAtividadeId, Integer exercicioId, Usuario usuario) {
        var exercicio = this.findExercicioById(exercicioId);

        locatorService.getRegistroAtividadeService(exercicio.getTipoExercicio())
            .excluirRegistro(registroAtividadeId, usuario);
    }

    private Exercicio findExercicioById(Integer exercicioId) {
        return exercicioService.findById(exercicioId);
    }
}
