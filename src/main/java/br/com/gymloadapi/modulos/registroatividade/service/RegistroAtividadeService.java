package br.com.gymloadapi.modulos.registroatividade.service;

import br.com.gymloadapi.modulos.comum.service.LocatorService;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeRequest;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistroAtividadeService {

    private final LocatorService locatorService;
    private final ExercicioService exercicioService;

    public void salvar(RegistroAtividadeRequest request, Usuario usuario) {
        var exercicio = exercicioService.findById(request.exercicioId());
        locatorService.getRegistroAtividadeService(exercicio.getTipoExercicio())
            .salvarRegistro(request, exercicio, usuario);
    }
}
