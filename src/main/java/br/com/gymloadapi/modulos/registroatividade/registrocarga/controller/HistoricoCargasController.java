package br.com.gymloadapi.modulos.registroatividade.registrocarga.controller;

import br.com.gymloadapi.modulos.registroatividade.registrocarga.dto.CargaResponse;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.dto.HistoricoCargasRequest;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.dto.HistoricoCargasResponse;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.service.RegistroCargaService;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/historico-cargas")
public class HistoricoCargasController {

    private final RegistroCargaService service;

    @PostMapping
    @ResponseStatus(CREATED)
    public void salvar(@RequestBody @Valid HistoricoCargasRequest request, @AuthenticationPrincipal Usuario usuario) {
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
