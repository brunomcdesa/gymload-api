package br.com.gymloadapi.modulos.registroatividade.controller;

import br.com.gymloadapi.modulos.registroatividade.dto.HistoricoRegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeRequest;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.service.RegistroAtividadeService;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/registro-atividades")
public class RegistroAtividadeController {

    private final RegistroAtividadeService service;

    @PostMapping
    @ResponseStatus(CREATED)
    public void salvar(@RequestBody @Valid RegistroAtividadeRequest request, @AuthenticationPrincipal Usuario usuario) {
        service.salvar(request, usuario);
    }

    @GetMapping("{exercicioId}")
    public RegistroAtividadeResponse buscarUltimoRegistroAtividade(@PathVariable Integer exercicioId,
                                                                   @AuthenticationPrincipal Usuario usuario) {
        return service.buscarUltimoRegistroAtividade(exercicioId, usuario.getId());
    }

    @GetMapping("{exercicioId}/completo")
    public List<HistoricoRegistroAtividadeResponse> buscarRegistroAtividadeCompleto(@PathVariable Integer exercicioId,
                                                                                    @AuthenticationPrincipal Usuario usuario) {
        return service.buscarRegistroAtividadeCompleto(exercicioId, usuario.getId());
    }
}
