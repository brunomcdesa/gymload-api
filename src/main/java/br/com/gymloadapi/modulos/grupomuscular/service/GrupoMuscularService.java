package br.com.gymloadapi.modulos.grupomuscular.service;

import br.com.gymloadapi.modulos.grupomuscular.model.GrupoMuscular;
import br.com.gymloadapi.modulos.grupomuscular.repository.GrupoMuscularRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GrupoMuscularService {

    private final GrupoMuscularRepository repository;

    public GrupoMuscular findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grupo Muscular não encontrado"));
    }
}
