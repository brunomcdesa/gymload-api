package br.com.gymloadapi.modulos.historicocargas.mapper;

import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.historicocargas.dto.HistoricoCargasRequest;
import br.com.gymloadapi.modulos.historicocargas.dto.HistoricoCargasResponse;
import br.com.gymloadapi.modulos.historicocargas.model.HistoricoCargas;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface HistoricoCargasMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercicio", source = "exercicio")
    @Mapping(target = "usuario", source = "usuario")
    HistoricoCargas mapToModel(HistoricoCargasRequest request, Exercicio exercicio, Usuario usuario);

    @Mapping(target = "exercicioNome", source = "historicoCargas.exercicio.nome")
    @Mapping(target = "tipoExercicio", source = "historicoCargas.exercicio.tipoExercicio")
    @Mapping(target = "grupoMuscularNome", source = "historicoCargas.exercicio.grupoMuscular.nome")
    @Mapping(target = "carga", expression = "java(historicoCargas.getCargaComUnidadePeso())")
    HistoricoCargasResponse mapToResponse(HistoricoCargas historicoCargas);
}
