package br.com.gymloadapi.modulos.treino.controller;

import br.com.gymloadapi.modulos.comum.enums.ESituacao;
import br.com.gymloadapi.modulos.treino.dto.TreinoRequest;
import br.com.gymloadapi.modulos.treino.dto.TreinoResponse;
import br.com.gymloadapi.modulos.treino.service.TreinoSessaoService;
import br.com.gymloadapi.modulos.treino.service.TreinoService;
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
@RequestMapping("api/treinos")
public class TreinoController {

    private final TreinoService service;
    private final TreinoSessaoService treinoSessaoService;

    @PostMapping
    @ResponseStatus(CREATED)
    public void salvar(@RequestBody @Valid TreinoRequest request,
                       @AuthenticationPrincipal Usuario usuario) {
        service.salvar(request, usuario);
    }

    @GetMapping
    public List<TreinoResponse> listarTodosDoUsuario(@AuthenticationPrincipal Usuario usuario,
                                                     @RequestParam(defaultValue = "false") boolean buscarInativos) {
        var situacao = buscarInativos ? ESituacao.INATIVO : ESituacao.ATIVO;
        return service.listarPorSituacao(usuario.getId(), situacao);
    }

    @PutMapping("{id}/editar")
    @ResponseStatus(NO_CONTENT)
    public void editar(@PathVariable Integer id, @RequestBody @Valid TreinoRequest request,
                       @AuthenticationPrincipal Usuario usuario) {
        service.editar(id, request, usuario.getId());
    }

    @PutMapping("{id}/ativar")
    @ResponseStatus(NO_CONTENT)
    public void ativar(@PathVariable Integer id, @AuthenticationPrincipal Usuario usuario) {
        service.ativar(id, usuario.getId());
    }

    @PutMapping("{id}/inativar")
    @ResponseStatus(NO_CONTENT)
    public void inativar(@PathVariable Integer id, @AuthenticationPrincipal Usuario usuario ) {
        service.inativar(id, usuario.getId());
    }

    @PostMapping("{id}/finalizar")
    @ResponseStatus(NO_CONTENT)
    public void finalizar(@PathVariable Integer id, @AuthenticationPrincipal Usuario usuario) {
        treinoSessaoService.finalizar(id, usuario);
    }
}
