package br.com.gymloadapi.modulos.treino.controller;

import br.com.gymloadapi.modulos.treino.dto.TreinoRequest;
import br.com.gymloadapi.modulos.treino.dto.TreinoResponse;
import br.com.gymloadapi.modulos.treino.service.TreinoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/treinos")
public class TreinoController {

    private final TreinoService service;

    @PostMapping
    @ResponseStatus(CREATED)
    public void salvar(@RequestBody @Valid TreinoRequest request) {
        service.salvar(request);
    }

    @GetMapping
    public List<TreinoResponse> listarTodosDoUsuario() {
        return service.listarTodosDoUsuario();
    }
}
