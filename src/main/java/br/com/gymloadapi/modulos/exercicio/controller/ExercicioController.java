package br.com.gymloadapi.modulos.exercicio.controller;

import br.com.gymloadapi.modulos.comum.dto.SelectResponse;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioFiltro;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioRequest;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioResponse;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/exercicios")
public class ExercicioController {

    private final ExercicioService service;

    @PostMapping
    @ResponseStatus(CREATED)
    public void salvar(@RequestBody @Valid ExercicioRequest request) {
        service.salvar(request);
    }

    @GetMapping
    public List<ExercicioResponse> buscarTodos(ExercicioFiltro filtros) {
        return service.buscarTodos(filtros);
    }

    @GetMapping("select")
    public List<SelectResponse> buscarTodosSelect() {
        return service.buscarTodosSelect();
    }

    @GetMapping("treino/{treinoId}")
    public List<ExercicioResponse> buscarExerciciosPorTreino(@PathVariable Integer treinoId) {
        return service.buscarExerciciosPorTreino(treinoId);
    }
}
