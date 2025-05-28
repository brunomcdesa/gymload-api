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
import org.springframework.web.multipart.MultipartFile;

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
    public void cadastrar(@RequestPart @Validated(Cadastro.class) UsuarioRequest usuarioRequest,
                          @RequestPart(required = false) MultipartFile imagem) {
        service.cadastrar(usuarioRequest, false, imagem, null);
    }

    @ResponseStatus(CREATED)
    @PostMapping("cadastro/admin")
    public void cadastrarAdmin(@RequestBody @Validated(Cadastro.class) UsuarioRequest usuarioRequest,
                               @AuthenticationPrincipal Usuario usuario) {
        service.cadastrar(usuarioRequest, true, null, usuario);
    }

    @ResponseStatus(NO_CONTENT)
    @PutMapping("{uuid}/editar")
    public void editar(@PathVariable UUID uuid, @RequestPart @Validated(Alteracao.class) UsuarioRequest usuarioRequest,
                       @RequestPart(required = false) MultipartFile imagem, @AuthenticationPrincipal Usuario usuario) {
        service.editar(uuid, usuarioRequest, imagem, usuario);
    }

    @GetMapping("{uuid}/detalhar")
    public UsuarioResponse detalharByUuid(@PathVariable UUID uuid) {
        return service.buscarPorUuid(uuid);
    }

    @GetMapping("url-imagem-perfil")
    public String buscarUrlImagemPerfil(@AuthenticationPrincipal Usuario usuario) {
        return service.buscarUrlImagemPerfil(usuario);
    }
}
