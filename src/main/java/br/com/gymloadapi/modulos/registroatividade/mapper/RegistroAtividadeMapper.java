package br.com.gymloadapi.modulos.registroatividade.mapper;

import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.registroatividade.dto.HistoricoRegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeRequest;
import br.com.gymloadapi.modulos.registroatividade.registrocardio.model.RegistroCardio;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.model.RegistroCarga;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import org.mapstruct.*;

@Mapper
public interface RegistroAtividadeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercicio", source = "exercicio")
    @Mapping(target = "usuario", source = "usuario")
    @Mapping(target = "dataCadastro", expression = "java(LocalDate.now())")
    RegistroCarga mapToRegistroCarga(RegistroAtividadeRequest request, Exercicio exercicio, Usuario usuario);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercicio", source = "exercicio")
    @Mapping(target = "usuario", source = "usuario")
    @Mapping(target = "dataCadastro", expression = "java(LocalDate.now())")
    RegistroCardio mapToRegistroCardio(RegistroAtividadeRequest request, Exercicio exercicio, Usuario usuario);

    @Mapping(target = "exercicioNome", source = "registroCarga.exercicio.nome")
    @Mapping(target = "tipoExercicio", source = "registroCarga.exercicio.tipoEquipamento")
    @Mapping(target = "grupoMuscularNome", source = "registroCarga.exercicio.grupoMuscular.nome")
    @Mapping(target = "carga", expression = "java(registroCarga.getCargaComUnidadePeso())")
    HistoricoRegistroAtividadeResponse mapToHistoricoRegistroAtividadeMusculacaoResponse(RegistroCarga registroCarga);

    @Mapping(target = "exercicioNome", source = "registroCardio.exercicio.nome")
    @Mapping(target = "velocidadeMedia", expression = "java(registroCardio.getVelocidadeMedia())")
    HistoricoRegistroAtividadeResponse mapToHistoricoRegistroAtividadeAerobicoResponse(RegistroCardio registroCardio);

    @BeanMapping(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
    void editarRegistroCarga(RegistroAtividadeRequest request, @MappingTarget RegistroCarga registroCarga);

    @BeanMapping(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
    void editarRegistroCarga(RegistroAtividadeRequest request, @MappingTarget RegistroCardio registroCardio);
}
