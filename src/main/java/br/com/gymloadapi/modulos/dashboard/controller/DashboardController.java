package br.com.gymloadapi.modulos.dashboard.controller;

import br.com.gymloadapi.modulos.dashboard.dto.DashboardStatsResponse;
import br.com.gymloadapi.modulos.dashboard.service.DashboardService;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/dashboard")
public class DashboardController {

    private final DashboardService service;

    @GetMapping("stats")
    public DashboardStatsResponse buscarStats(@AuthenticationPrincipal Usuario usuario) {
        return service.buscarStats(usuario.getId());
    }
}
