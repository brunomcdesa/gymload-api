package br.com.gymloadapi.modulos.treino.controller;

import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Alteracao;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Cadastro;
import br.com.gymloadapi.modulos.treino.dto.TreinoRequest;
import br.com.gymloadapi.modulos.treino.dto.TreinoResponse;
import br.com.gymloadapi.modulos.treino.service.TreinoService;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/treinos")
public class TreinoController {

    private final TreinoService service;

    @PostMapping
    @ResponseStatus(CREATED)
    public void salvar(@RequestBody @Validated(Cadastro.class) TreinoRequest request,
                       @AuthenticationPrincipal Usuario usuario) {
        service.salvar(request, usuario);
    }

    @GetMapping
    public List<TreinoResponse> listarTodosDoUsuario(@AuthenticationPrincipal Usuario usuario) {
        return service.listarTodosDoUsuario(usuario.getId());
    }

    @PutMapping("{id}/editar")
    @ResponseStatus(NO_CONTENT)
    public void editar(@PathVariable Integer id, @RequestBody @Validated(Alteracao.class) TreinoRequest request) {
        service.editar(id, request);
    }

    @PutMapping("{id}/ativar")
    @ResponseStatus(NO_CONTENT)
    public void ativar(@PathVariable Integer id) {
        service.ativar(id);
    }

    @PutMapping("{id}/inativar")
    @ResponseStatus(NO_CONTENT)
    public void inativar(@PathVariable Integer id) {
        service.inativar(id);
    }
}
