package br.com.gymloadapi.modulos.exercicio.controller;

import br.com.gymloadapi.modulos.exercicio.dto.ExercicioRequest;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/exercicios")
public class ExercicioController {

    private final ExercicioService service;

    @PostMapping
    @ResponseStatus(CREATED)
    public void salvar(@RequestBody ExercicioRequest request) {
        service.salvar(request);
    }

    @GetMapping
    public List<Exercicio> buscarTodos() {
        return service.buscarTodos();
    }
}
