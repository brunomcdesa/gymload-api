package br.com.gymloadapi.modulos.comum.controller;

import br.com.gymloadapi.modulos.comum.dto.SelectResponse;
import br.com.gymloadapi.modulos.comum.service.EnumService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/enums")
public class EnumController {

    private final EnumService enumService;

    @GetMapping("tipos-exercicios/select")
    public List<SelectResponse> getTiposExerciciosSelect() {
        return enumService.getTiposExerciciosSelect();
    }

    @GetMapping("tipos-pegadas/select")
    public List<SelectResponse> getTiposPegadasSelect() {
        return enumService.getTiposPegadasSelect();
    }

    @GetMapping("unidades-pesos/select")
    public List<SelectResponse> getUnidadesPesosSelect() {
        return enumService.getUnidadesPesosSelect();
    }
}
