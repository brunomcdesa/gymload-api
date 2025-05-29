package br.com.gymloadapi.modulos.registroatividade.mapper;

import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.registroatividade.dto.HistoricoRegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeRequest;
import br.com.gymloadapi.modulos.registroatividade.registroaerobico.model.RegistroAerobico;
import br.com.gymloadapi.modulos.registroatividade.registromusculacao.model.RegistroMusculacao;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RegistroAtividadeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercicio", source = "exercicio")
    @Mapping(target = "usuario", source = "usuario")
    @Mapping(target = "dataCadastro", expression = "java(LocalDate.now())")
    RegistroMusculacao mapToRegistroMusculacao(RegistroAtividadeRequest request, Exercicio exercicio, Usuario usuario);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercicio", source = "exercicio")
    @Mapping(target = "usuario", source = "usuario")
    @Mapping(target = "dataCadastro", expression = "java(LocalDate.now())")
    RegistroAerobico mapToRegistroAerobico(RegistroAtividadeRequest request, Exercicio exercicio, Usuario usuario);

    @Mapping(target = "duracao", ignore = true)
    @Mapping(target = "distancia", ignore = true)
    @Mapping(target = "velocidadeMedia", ignore = true)
    @Mapping(target = "exercicioNome", source = "registroMusculacao.exercicio.nome")
    @Mapping(target = "tipoExercicio", source = "registroMusculacao.exercicio.tipoEquipamento")
    @Mapping(target = "grupoMuscularNome", source = "registroMusculacao.exercicio.grupoMuscular.nome")
    @Mapping(target = "carga", expression = "java(registroMusculacao.getPesoComUnidadePeso())")
    HistoricoRegistroAtividadeResponse mapToHistoricoRegistroAtividadeMusculacaoResponse(RegistroMusculacao registroMusculacao);

    @Mapping(target = "peso", ignore = true)
    @Mapping(target = "carga", ignore = true)
    @Mapping(target = "qtdSeries", ignore = true)
    @Mapping(target = "unidadePeso", ignore = true)
    @Mapping(target = "tipoExercicio", ignore = true)
    @Mapping(target = "qtdRepeticoes", ignore = true)
    @Mapping(target = "grupoMuscularNome", ignore = true)
    @Mapping(target = "exercicioNome", source = "registroAerobico.exercicio.nome")
    @Mapping(target = "velocidadeMedia", expression = "java(registroAerobico.getVelocidadeMedia())")
    HistoricoRegistroAtividadeResponse mapToHistoricoRegistroAtividadeAerobicoResponse(RegistroAerobico registroAerobico);

    @BeanMapping(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
    void editarRegistroMusculacao(RegistroAtividadeRequest request, @MappingTarget RegistroMusculacao registroMusculacao);

    @BeanMapping(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
    void editarRegistroAerobico(RegistroAtividadeRequest request, @MappingTarget RegistroAerobico registroAerobico);
}
