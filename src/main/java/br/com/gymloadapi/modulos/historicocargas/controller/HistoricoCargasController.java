package br.com.gymloadapi.modulos.historicocargas.controller;

import br.com.gymloadapi.modulos.historicocargas.dto.HistoricoCargasRequest;
import br.com.gymloadapi.modulos.historicocargas.dto.HistoricoCargasResponse;
import br.com.gymloadapi.modulos.historicocargas.service.HistoricoCargasService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/historico-cargas")
public class HistoricoCargasController {

    private final HistoricoCargasService service;

    @PostMapping
    @ResponseStatus(CREATED)
    public void salvar(@RequestBody HistoricoCargasRequest request) {
        service.salvar(request);
    }

    @GetMapping("{exercicioId}")
    public List<HistoricoCargasResponse> buscarExerciciosDoUsuario(@PathVariable Integer exercicioId) {
        return service.buscarExerciciosDoUsuario(exercicioId);
    }
}
