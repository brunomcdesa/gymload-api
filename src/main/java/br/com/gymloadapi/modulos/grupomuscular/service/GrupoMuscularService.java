package br.com.gymloadapi.modulos.grupomuscular.service;

import br.com.gymloadapi.modulos.comum.dto.SelectResponse;
import br.com.gymloadapi.modulos.grupomuscular.dto.GrupoMuscularRequest;
import br.com.gymloadapi.modulos.grupomuscular.dto.GrupoMuscularResponse;
import br.com.gymloadapi.modulos.grupomuscular.mapper.GrupoMuscularMapper;
import br.com.gymloadapi.modulos.grupomuscular.model.GrupoMuscular;
import br.com.gymloadapi.modulos.grupomuscular.repository.GrupoMuscularRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GrupoMuscularService {

    private final GrupoMuscularMapper mapper;
    private final GrupoMuscularRepository repository;

    public void salvar(GrupoMuscularRequest request) {
        repository.save(mapper.mapToModel(request));
    }

    public List<GrupoMuscularResponse> findAllResponse() {
        return this.findAll().stream()
            .map(mapper::mapToResponse)
            .toList();
    }

    public List<SelectResponse> findAllSelect() {
        return this.findAll().stream()
            .map(mapper::mapToSelectResponse)
            .toList();
    }

    private List<GrupoMuscular> findAll() {
        return repository.findAll();
    }

    public GrupoMuscular findById(Integer id) {
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Grupo Muscular não encontrado"));
    }
}
