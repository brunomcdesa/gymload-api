package br.com.gymloadapi.modulos.exercicio.mapper;

import br.com.gymloadapi.modulos.comum.dto.SelectResponse;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioRequest;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioResponse;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.grupomuscular.model.GrupoMuscular;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ExercicioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nome", source = "request.nome")
    @Mapping(target = "grupoMuscular", source = "grupoMuscular")
    Exercicio mapToModel(ExercicioRequest request, GrupoMuscular grupoMuscular);

    @Mapping(target = "grupoMuscularNome", source = "exercicio.grupoMuscular.nome")
    ExercicioResponse mapModelToResponse(Exercicio exercicio);

    @Mapping(target = "value", source = "exercicio.nome")
    SelectResponse mapToSelectResponse(Exercicio exercicio);
}
