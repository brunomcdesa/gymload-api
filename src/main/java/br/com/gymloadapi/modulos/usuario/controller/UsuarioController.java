package br.com.gymloadapi.modulos.usuario.controller;

import br.com.gymloadapi.autenticacao.dto.CadastroRequest;
import br.com.gymloadapi.modulos.usuario.dto.UsuarioResponse;
import br.com.gymloadapi.modulos.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    @GetMapping
    public List<UsuarioResponse> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("cadastro/admin")
    public void cadastrarAdmin(@RequestBody @Valid CadastroRequest cadastroRequest) {
        service.cadastrar(cadastroRequest);
    }
}
