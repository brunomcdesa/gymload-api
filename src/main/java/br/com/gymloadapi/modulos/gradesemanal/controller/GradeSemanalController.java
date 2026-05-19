package br.com.gymloadapi.modulos.gradesemanal.controller;

import br.com.gymloadapi.modulos.comum.enums.EDiaSemana;
import br.com.gymloadapi.modulos.gradesemanal.dto.GradeSemanalHojeResponse;
import br.com.gymloadapi.modulos.gradesemanal.dto.GradeSemanalRequest;
import br.com.gymloadapi.modulos.gradesemanal.dto.GradeSemanalResponse;
import br.com.gymloadapi.modulos.gradesemanal.service.GradeSemanalService;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/grade-semanal")
public class GradeSemanalController {

    private final GradeSemanalService service;

    @GetMapping("hoje")
    public ResponseEntity<GradeSemanalHojeResponse> buscarHoje(@AuthenticationPrincipal Usuario usuario) {
        return service.buscarHoje(usuario.getId())
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping
    public List<GradeSemanalResponse> listar(@AuthenticationPrincipal Usuario usuario) {
        return service.listar(usuario.getId());
    }

    @PutMapping("{diaSemana}")
    @ResponseStatus(NO_CONTENT)
    public void salvar(@PathVariable EDiaSemana diaSemana,
                       @RequestBody @Valid GradeSemanalRequest request,
                       @AuthenticationPrincipal Usuario usuario) {
        service.salvar(diaSemana, request, usuario);
    }

    @DeleteMapping("{diaSemana}")
    @ResponseStatus(NO_CONTENT)
    public void remover(@PathVariable EDiaSemana diaSemana,
                        @AuthenticationPrincipal Usuario usuario) {
        service.remover(diaSemana, usuario.getId());
    }
}
