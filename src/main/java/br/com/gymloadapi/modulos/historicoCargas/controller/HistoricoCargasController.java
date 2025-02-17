package br.com.gymloadapi.modulos.historicoCargas.controller;

import br.com.gymloadapi.modulos.historicoCargas.dto.HistoricoCargasRequest;
import br.com.gymloadapi.modulos.historicoCargas.dto.HistoricoCargasResponse;
import br.com.gymloadapi.modulos.historicoCargas.service.HistoricoCargasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    public List<HistoricoCargasResponse> findAll() {
        return service.findAll();
    }
}
