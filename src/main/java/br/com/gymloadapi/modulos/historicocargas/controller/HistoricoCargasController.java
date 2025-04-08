package br.com.gymloadapi.modulos.historicocargas.controller;

import br.com.gymloadapi.modulos.historicocargas.dto.CargaResponse;
import br.com.gymloadapi.modulos.historicocargas.dto.HistoricoCargasRequest;
import br.com.gymloadapi.modulos.historicocargas.dto.HistoricoCargasResponse;
import br.com.gymloadapi.modulos.historicocargas.service.HistoricoCargasService;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public void salvar(@RequestBody HistoricoCargasRequest request, @AuthenticationPrincipal Usuario usuario) {
        service.salvar(request, usuario);
    }

    @GetMapping("{exercicioId}")
    public CargaResponse buscarUltimoHistoricoCargas(@PathVariable Integer exercicioId,
                                                     @AuthenticationPrincipal Usuario usuario) {
        return service.buscarUltimoHistoricoCargas(exercicioId, usuario.getId());
    }

    @GetMapping("{exercicioId}/completo")
    public List<HistoricoCargasResponse> buscarHistoricoCargasCompleto(@PathVariable Integer exercicioId,
                                                                       @AuthenticationPrincipal Usuario usuario) {
        return service.buscarHistoricoCargasCompleto(exercicioId, usuario.getId());
    }
}
