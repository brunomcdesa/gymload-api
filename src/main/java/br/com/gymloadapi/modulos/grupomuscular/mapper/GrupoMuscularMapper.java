package br.com.gymloadapi.modulos.grupomuscular.mapper;

import br.com.gymloadapi.modulos.grupomuscular.dto.GrupoMuscularRequest;
import br.com.gymloadapi.modulos.grupomuscular.model.GrupoMuscular;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface GrupoMuscularMapper {

    @Mapping(target = "id", ignore = true)
    GrupoMuscular mapToModel(GrupoMuscularRequest request);
}
