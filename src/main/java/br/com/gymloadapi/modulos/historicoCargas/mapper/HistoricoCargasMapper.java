package br.com.gymloadapi.modulos.historicoCargas.mapper;

import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.historicoCargas.dto.HistoricoCargasRequest;
import br.com.gymloadapi.modulos.historicoCargas.dto.HistoricoCargasResponse;
import br.com.gymloadapi.modulos.historicoCargas.model.HistoricoCargas;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface HistoricoCargasMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercicio", source = "exercicio")
    HistoricoCargas mapToModel(HistoricoCargasRequest request, Exercicio exercicio);

    @Mapping(target = "exercicioNome", source = "historicoCargas.exercicio.nome")
    @Mapping(target = "tipoExercicio", source = "historicoCargas.exercicio.tipoExercicio")
    @Mapping(target = "grupoMuscularNome", source = "historicoCargas.exercicio.grupoMuscular.nome")
    @Mapping(target = "carga", expression = "java(historicoCargas.getCargaComUnidadePeso())")
    HistoricoCargasResponse mapToResponse(HistoricoCargas historicoCargas);
}
