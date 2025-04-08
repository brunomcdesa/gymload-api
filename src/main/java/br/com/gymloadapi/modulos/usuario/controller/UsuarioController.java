package br.com.gymloadapi.modulos.usuario.controller;

import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Alteracao;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Cadastro;
import br.com.gymloadapi.modulos.usuario.dto.UsuarioRequest;
import br.com.gymloadapi.modulos.usuario.dto.UsuarioResponse;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import br.com.gymloadapi.modulos.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    @GetMapping
    public List<UsuarioResponse> buscarTodos() {
        return service.buscarTodos();
    }

    @ResponseStatus(CREATED)
    @PostMapping("cadastro")
    public void cadastrar(@RequestBody @Validated(Cadastro.class) UsuarioRequest usuarioRequest) {
        service.cadastrar(usuarioRequest, false);
    }

    @ResponseStatus(CREATED)
    @PostMapping("cadastro/admin")
    public void cadastrarAdmin(@RequestBody @Validated(Cadastro.class) UsuarioRequest usuarioRequest) {
        service.cadastrar(usuarioRequest, true);
    }

    @ResponseStatus(NO_CONTENT)
    @PutMapping("{id}/editar")
    public void editar(@PathVariable UUID id, @RequestBody @Validated(Alteracao.class) UsuarioRequest usuarioRequest,
                       @AuthenticationPrincipal Usuario usuario) {
        service.editar(id, usuarioRequest, usuario);
    }
}
