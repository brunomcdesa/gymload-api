package br.com.gymloadapi.modulos.registroatividade.mapper;

import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.registroatividade.dto.HistoricoRegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeRequest;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.registroaerobico.model.RegistroAerobico;
import br.com.gymloadapi.modulos.registroatividade.registrocalistenia.model.RegistroCalistenia;
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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercicio", source = "exercicio")
    @Mapping(target = "usuario", source = "usuario")
    @Mapping(target = "dataCadastro", expression = "java(LocalDate.now())")
    @Mapping(target = "pesoAdicional", source = "request.peso")
    RegistroCalistenia mapToRegistroCalistenia(RegistroAtividadeRequest request, Exercicio exercicio, Usuario usuario);

    @Mapping(target = "duracao", ignore = true)
    @Mapping(target = "distancia", ignore = true)
    @Mapping(target = "velocidadeMedia", ignore = true)
    @Mapping(target = "exercicioNome", source = "registroMusculacao.exercicio.nome")
    @Mapping(target = "tipoEquipamento", source = "registroMusculacao.exercicio.tipoEquipamento")
    @Mapping(target = "grupoMuscularNome", source = "registroMusculacao.exercicio.grupoMuscular.nome")
    @Mapping(target = "carga", expression = "java(registroMusculacao.getPesoComUnidadePeso())")
    HistoricoRegistroAtividadeResponse mapToHistoricoRegistroAtividadeMusculacaoResponse(RegistroMusculacao registroMusculacao);

    @Mapping(target = "peso", ignore = true)
    @Mapping(target = "carga", ignore = true)
    @Mapping(target = "qtdSeries", ignore = true)
    @Mapping(target = "unidadePeso", ignore = true)
    @Mapping(target = "tipoEquipamento", ignore = true)
    @Mapping(target = "qtdRepeticoes", ignore = true)
    @Mapping(target = "grupoMuscularNome", ignore = true)
    @Mapping(target = "exercicioNome", source = "registroAerobico.exercicio.nome")
    @Mapping(target = "velocidadeMedia", expression = "java(registroAerobico.getVelocidadeMedia())")
    HistoricoRegistroAtividadeResponse mapToHistoricoRegistroAtividadeAerobicoResponse(RegistroAerobico registroAerobico);

    @Mapping(target = "duracao", ignore = true)
    @Mapping(target = "distancia", ignore = true)
    @Mapping(target = "velocidadeMedia", ignore = true)
    @Mapping(target = "peso", source = "registroCalistenia.pesoAdicional")
    @Mapping(target = "exercicioNome", source = "registroCalistenia.exercicio.nome")
    @Mapping(target = "tipoEquipamento", source = "registroCalistenia.exercicio.tipoEquipamento")
    @Mapping(target = "carga", expression = "java(registroCalistenia.getPesoComUnidadePeso())")
    @Mapping(target = "grupoMuscularNome", source = "registroCalistenia.exercicio.grupoMuscular.nome")
    HistoricoRegistroAtividadeResponse mapToHistoricoRegistroAtividadeCalisteniaResponse(RegistroCalistenia registroCalistenia);

    @BeanMapping(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
    void editarRegistroMusculacao(RegistroAtividadeRequest request, @MappingTarget RegistroMusculacao registroMusculacao);

    @BeanMapping(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
    void editarRegistroAerobico(RegistroAtividadeRequest request, @MappingTarget RegistroAerobico registroAerobico);

    @Mapping(target = "pesoAdicional", source = "request.peso")
    @Mapping(target = "unidadePeso", source = "request.unidadePeso")
    @BeanMapping(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
    void editarRegistroCalistenia(RegistroAtividadeRequest request, @MappingTarget RegistroCalistenia registroCalistenia);

    RegistroAtividadeResponse mapToRegistroAtividadeResponse(Integer exercicioId, String destaque, String ultimoPeso,
                                                             String ultimaDistancia, Integer ultimaQtdMaxRepeticoes);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCadastro", expression = "java(LocalDate.now())")
    RegistroMusculacao copiarRegistroMusculacao(RegistroMusculacao registroMusculacao);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCadastro", expression = "java(LocalDate.now())")
    RegistroAerobico copiarRegistroAerobico(RegistroAerobico registroMusculacao);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCadastro", expression = "java(LocalDate.now())")
    RegistroCalistenia copiarRegistroCalistenia(RegistroCalistenia registroMusculacao);
}
