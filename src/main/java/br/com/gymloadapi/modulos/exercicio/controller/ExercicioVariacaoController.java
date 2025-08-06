package br.com.gymloadapi.modulos.exercicio.controller;

import br.com.gymloadapi.modulos.exercicio.dto.ExercicioVariacaoRequest;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioVariacaoResponse;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioVariacaoService;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/exercicios-variacoes")
public class ExercicioVariacaoController {

    private final ExercicioVariacaoService service;

    @PostMapping
    @ResponseStatus(CREATED)
    public void salvar(@RequestBody @Valid ExercicioVariacaoRequest request, @AuthenticationPrincipal Usuario usuario) {
        service.salvar(request, usuario.getId());
    }

    @GetMapping("{exercicioId}")
    public List<ExercicioVariacaoResponse> buscarVariacoesDoExercicio(@PathVariable Integer exercicioId) {
        return service.buscarVariacoesDoExercicio(exercicioId);
    }
}
