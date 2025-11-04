package br.com.gymloadapi.modulos.tipovariacao.controller;

import br.com.gymloadapi.modulos.comum.dto.SelectResponse;
import br.com.gymloadapi.modulos.tipovariacao.dto.TipoVariacaoRequest;
import br.com.gymloadapi.modulos.tipovariacao.dto.TipoVariacaoResponse;
import br.com.gymloadapi.modulos.tipovariacao.service.TipoVariacaoService;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/tipos-variacoes")
public class TipoVariacaoController {

    private final TipoVariacaoService service;

    @PostMapping
    @ResponseStatus(CREATED)
    public void salvar(@RequestBody @Valid TipoVariacaoRequest request, @AuthenticationPrincipal Usuario usuarioAutenticado) {
        service.salvar(request, usuarioAutenticado);
    }

    @GetMapping
    public List<TipoVariacaoResponse> buscarTodos() {
        return service.buscarTodos();
    }

    @PutMapping("{id}/editar")
    @ResponseStatus(NO_CONTENT)
    public void editar(@PathVariable Integer id, @RequestBody @Valid TipoVariacaoRequest request,
                       @AuthenticationPrincipal Usuario usuarioAutenticado) {
        service.editar(id, request, usuarioAutenticado);
    }

    @GetMapping("select")
    public List<SelectResponse> getSelect() {
        return service.getSelect();
    }
}
