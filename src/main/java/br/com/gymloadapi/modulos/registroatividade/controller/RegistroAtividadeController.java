package br.com.gymloadapi.modulos.registroatividade.controller;

import br.com.gymloadapi.modulos.registroatividade.dto.HistoricoRegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeFiltros;
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
import static org.springframework.http.HttpStatus.NO_CONTENT;

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

    @GetMapping("destaques")
    public List<RegistroAtividadeResponse> buscarDestaques(@Valid RegistroAtividadeFiltros filtros,
                                                           @AuthenticationPrincipal Usuario usuario) {
        return service.buscarDestaques(filtros, usuario.getId());
    }

    @GetMapping("{exercicioId}/completo")
    public List<HistoricoRegistroAtividadeResponse> buscarRegistroAtividadeCompleto(@PathVariable Integer exercicioId,
                                                                                    @AuthenticationPrincipal Usuario usuario) {
        return service.buscarRegistroAtividadeCompleto(exercicioId, usuario.getId());
    }

    @PutMapping("{id}/editar")
    @ResponseStatus(NO_CONTENT)
    public void editar(@PathVariable Integer id,
                       @RequestBody @Valid RegistroAtividadeRequest request, @AuthenticationPrincipal Usuario usuario) {
        service.editar(id, request, usuario);
    }
}
