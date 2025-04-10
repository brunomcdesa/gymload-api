package br.com.gymloadapi.autenticacao.controller;

import br.com.gymloadapi.autenticacao.dto.LoginRequest;
import br.com.gymloadapi.autenticacao.dto.LoginResponse;
import br.com.gymloadapi.autenticacao.service.AutenticacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final AutenticacaoService service;

    @PostMapping("login")
    public LoginResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        return service.login(loginRequest);
    }

    @PutMapping("alterar-senha")
    @ResponseStatus(NO_CONTENT)
    public void alterarSenha(@RequestBody @Valid LoginRequest loginRequest) {
        service.alterarSenha(loginRequest);
    }
}
