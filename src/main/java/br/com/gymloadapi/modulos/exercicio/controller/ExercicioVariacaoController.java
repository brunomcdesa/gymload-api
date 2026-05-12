package br.com.gymloadapi.modulos.exercicio.controller;

import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Alteracao;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Cadastro;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioVariacaoRequest;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioVariacaoResponse;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioVariacaoService;
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
@RequestMapping("api/exercicios-variacoes")
public class ExercicioVariacaoController {

    private final ExercicioVariacaoService service;

    @PostMapping
    @ResponseStatus(CREATED)
    public void salvar(@RequestBody @Validated(Cadastro.class) ExercicioVariacaoRequest request,
                       @AuthenticationPrincipal Usuario usuario) {
        service.salvar(request, usuario.getId());
    }

    @GetMapping("{exercicioId}")
    public List<ExercicioVariacaoResponse> buscarVariacoesDoExercicio(@PathVariable Integer exercicioId,
                                                                      @AuthenticationPrincipal Usuario usuario) {
        return service.buscarVariacoesDoExercicio(exercicioId, usuario.getId());
    }

    @PutMapping("{id}/editar")
    @ResponseStatus(NO_CONTENT)
    public void editarVariacao(@PathVariable Integer id,
                               @RequestBody @Validated(Alteracao.class) ExercicioVariacaoRequest request,
                               @AuthenticationPrincipal Usuario usuario) {
        service.editarVariacao(id, request, usuario);
    }
}
