package br.com.gymloadapi.modulos.registroatividade.service;

import br.com.gymloadapi.modulos.comum.service.LocatorService;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.registroatividade.dto.HistoricoRegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeRequest;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeResponse;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistroAtividadeService {

    private final LocatorService locatorService;
    private final ExercicioService exercicioService;

    public void salvar(RegistroAtividadeRequest request, Usuario usuario) {
        var exercicio = this.findExercicioById(request.exercicioId());
        locatorService.getRegistroAtividadeService(exercicio.getTipoExercicio())
            .salvarRegistro(request, exercicio, usuario);
    }

    public RegistroAtividadeResponse buscarUltimoRegistroAtividade(Integer exercicioId, Integer usuarioId) {
        var exercicio = this.findExercicioById(exercicioId);

        return locatorService.getRegistroAtividadeService(exercicio.getTipoExercicio())
            .buscarUltimoRegistro(exercicioId, usuarioId);
    }

    public List<HistoricoRegistroAtividadeResponse> buscarRegistroAtividadeCompleto(Integer exercicioId, Integer usuarioId) {
        var exercicio = this.findExercicioById(exercicioId);

        return locatorService.getRegistroAtividadeService(exercicio.getTipoExercicio())
            .buscarHistoricoRegistroCompleto(exercicioId, usuarioId);
    }

    private Exercicio findExercicioById(Integer exercicioId) {
        return exercicioService.findById(exercicioId);
    }
}
