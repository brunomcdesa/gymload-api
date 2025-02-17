package br.com.gymloadapi.modulos.grupomuscular.controller;

import br.com.gymloadapi.modulos.comum.dto.SelectResponse;
import br.com.gymloadapi.modulos.grupomuscular.dto.GrupoMuscularRequest;
import br.com.gymloadapi.modulos.grupomuscular.model.GrupoMuscular;
import br.com.gymloadapi.modulos.grupomuscular.service.GrupoMuscularService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/grupos-musculares")
public class GrupoMuscularController {

    private final GrupoMuscularService service;

    @PostMapping
    @ResponseStatus(CREATED)
    public void salvar(@RequestBody GrupoMuscularRequest request) {
        service.salvar(request);
    }

    @GetMapping("select")
    public List<SelectResponse> findAllSelect() {
        return service.findAllSelect();
    }
}
