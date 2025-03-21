package br.com.gymloadapi.autenticacao.controller;

import br.com.gymloadapi.autenticacao.dto.CadastroRequest;
import br.com.gymloadapi.autenticacao.dto.LoginRequest;
import br.com.gymloadapi.autenticacao.dto.LoginResponse;
import br.com.gymloadapi.autenticacao.service.AutenticacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final AutenticacaoService service;

    @PostMapping("login")
    public LoginResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        return service.login(loginRequest);
    }

    @PostMapping("cadastro")
    @ResponseStatus(CREATED)
    public void cadastrar(@RequestBody @Valid CadastroRequest cadastroRequest) {
        service.cadastrarUsuario(cadastroRequest);
    }
}
