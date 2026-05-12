package br.com.gymloadapi.modulos.exercicio.mapper;

import br.com.gymloadapi.modulos.comum.dto.SelectResponse;
import br.com.gymloadapi.modulos.comum.enums.EAcao;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioRequest;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioResponse;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.exercicio.model.ExercicioHistorico;
import br.com.gymloadapi.modulos.exercicio.model.ExercicioVariacao;
import br.com.gymloadapi.modulos.exercicio.model.ExercicioVariacaoHistorico;
import br.com.gymloadapi.modulos.grupomuscular.model.GrupoMuscular;
import br.com.gymloadapi.modulos.tipovariacao.model.TipoVariacao;
import org.mapstruct.*;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class, Boolean.class})
public interface ExercicioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "treinos", ignore = true)
    @Mapping(target = "nome", source = "request.nome")
    @Mapping(target = "grupoMuscular", source = "grupoMuscular")
    @Mapping(target = "possuiVariacao", expression = "java(Boolean.valueOf(request.possuiVariacao()))")
    Exercicio mapToModel(ExercicioRequest request, GrupoMuscular grupoMuscular);

    @Mapping(target = "grupoMuscularId", source = "exercicio.grupoMuscular.id")
    @Mapping(target = "grupoMuscularNome", source = "exercicio.grupoMuscular.nome")
    ExercicioResponse mapModelToResponse(Exercicio exercicio);

    @Mapping(target = "value", source = "exercicio.id")
    @Mapping(target = "label", source = "exercicio.nome")
    SelectResponse mapToSelectResponse(Exercicio exercicio);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nome", source = "request.nome")
    @Mapping(target = "grupoMuscular", source = "grupoMuscular")
    @Mapping(target = "possuiVariacao", expression = "java(Boolean.valueOf(request.possuiVariacao()))")
    @BeanMapping(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
    void editarExercicio(ExercicioRequest request, GrupoMuscular grupoMuscular, @MappingTarget Exercicio exercicio);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "acao", source = "acao")
    @Mapping(target = "usuarioCadastroId", source = "usuarioId")
    @Mapping(target = "dataCadastro", expression = "java(LocalDateTime.now())")
    ExercicioHistorico mapToHistorico(Exercicio exercicio, Integer usuarioId, EAcao acao);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nome", source = "nomeVariacao")
    @Mapping(target = "exercicio", source = "exercicio")
    @Mapping(target = "tipoVariacao", source = "tipoVariacao")
    @Mapping(target = "usuarioCadastroId", source = "usuarioAutenticadoId")
    @Mapping(target = "dataCadastro", expression = "java(LocalDateTime.now())")
    ExercicioVariacao mapToExercicioVariacao(Exercicio exercicio, Integer usuarioAutenticadoId,
                                             TipoVariacao tipoVariacao, String nomeVariacao);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nome", source = "nomeVariacao")
    @Mapping(target = "tipoVariacao", source = "tipoVariacao")
    @BeanMapping(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
    void editarVariacao(TipoVariacao tipoVariacao, String nomeVariacao,
                        @MappingTarget ExercicioVariacao exercicioVariacao);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "acao", source = "acao")
    @Mapping(target = "usuarioCadastroId", source = "usuarioId")
    @Mapping(target = "dataCadastro", expression = "java(LocalDateTime.now())")
    ExercicioVariacaoHistorico mapToVariacaoHistorico(ExercicioVariacao exercicioVariacao, Integer usuarioId, EAcao acao);
}
