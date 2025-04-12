package br.com.gymloadapi.modulos.registroatividade.registrocarga.mapper;

import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeRequest;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.dto.HistoricoCargasRequest;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.dto.HistoricoCargasResponse;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.model.RegistroCarga;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;

@Mapper(imports = LocalDate.class)
public interface HistoricoCargasMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "peso", source = "request.carga")
    @Mapping(target = "exercicio", source = "exercicio")
    @Mapping(target = "usuario", source = "usuario")
    @Mapping(target = "dataCadastro", expression = "java(LocalDate.now())")
    RegistroCarga mapToModel(HistoricoCargasRequest request, Exercicio exercicio, Usuario usuario);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercicio", source = "exercicio")
    @Mapping(target = "usuario", source = "usuario")
    @Mapping(target = "dataCadastro", expression = "java(LocalDate.now())")
    RegistroCarga mapToModel(RegistroAtividadeRequest request, Exercicio exercicio, Usuario usuario);

    @Mapping(target = "exercicioNome", source = "registroCarga.exercicio.nome")
    @Mapping(target = "tipoExercicio", source = "registroCarga.exercicio.tipoEquipamento")
    @Mapping(target = "grupoMuscularNome", source = "registroCarga.exercicio.grupoMuscular.nome")
    @Mapping(target = "carga", expression = "java(registroCarga.getCargaComUnidadePeso())")
    HistoricoCargasResponse mapToResponse(RegistroCarga registroCarga);
}
