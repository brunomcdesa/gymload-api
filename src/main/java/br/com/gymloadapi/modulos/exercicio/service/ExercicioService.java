package br.com.gymloadapi.modulos.exercicio.service;

import br.com.gymloadapi.modulos.exercicio.dto.ExercicioRequest;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.exercicio.repository.ExercicioRepository;
import br.com.gymloadapi.modulos.grupomuscular.service.GrupoMuscularService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExercicioService {

    private final ExercicioRepository repository;
    private final GrupoMuscularService grupoMuscularService;

    public void salvar(ExercicioRequest request) {
        var grupoMuscular = grupoMuscularService.findById(request.grupoMuscularId());
        repository.save(Exercicio.of(request, grupoMuscular));
    }

    public List<Exercicio> buscarTodos() {
        return repository.findAll();
    }
}
