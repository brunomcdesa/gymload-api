package br.com.gymloadapi.modulos.exercicio.mapper;

import br.com.gymloadapi.modulos.comum.dto.SelectResponse;
import br.com.gymloadapi.modulos.comum.enums.EAcao;
import br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioRequest;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioResponse;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioVariacaoResponse;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.exercicio.model.ExercicioHistorico;
import br.com.gymloadapi.modulos.exercicio.model.ExercicioVariacao;
import br.com.gymloadapi.modulos.grupomuscular.model.GrupoMuscular;
import org.mapstruct.*;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = LocalDateTime.class)
public interface ExercicioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "treinos", ignore = true)
    @Mapping(target = "nome", source = "request.nome")
    @Mapping(target = "grupoMuscular", source = "grupoMuscular")
    Exercicio mapToModel(ExercicioRequest request, GrupoMuscular grupoMuscular);

    @Mapping(target = "grupoMuscularId", source = "exercicio.grupoMuscular.id")
    @Mapping(target = "grupoMuscularNome", source = "exercicio.grupoMuscular.nome")
    ExercicioResponse mapModelToResponse(Exercicio exercicio);

    @Mapping(target = "value", source = "exercicio.id")
    @Mapping(target = "label", expression = "java(exercicio.getNomeComTipoEquipamento())")
    SelectResponse mapToSelectResponse(Exercicio exercicio);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nome", source = "request.nome")
    @Mapping(target = "grupoMuscular", source = "grupoMuscular")
    @BeanMapping(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
    void editar(ExercicioRequest request, GrupoMuscular grupoMuscular, @MappingTarget Exercicio exercicio);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "acao", source = "acao")
    @Mapping(target = "usuarioCadastroId", source = "usuarioId")
    @Mapping(target = "dataCadastro", expression = "java(LocalDateTime.now())")
    ExercicioHistorico mapToHistorico(Exercicio exercicio, Integer usuarioId, EAcao acao);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nome", source = "nomeVariacao")
    @Mapping(target = "exercicio", source = "exercicio")
    @Mapping(target = "tipoEquipamento", source = "tipoEquipamento")
    @Mapping(target = "usuarioCadastroId", source = "usuarioAutenticadoId")
    @Mapping(target = "dataCadastro", expression = "java(LocalDateTime.now())")
    ExercicioVariacao mapToExercicioVariacao(Exercicio exercicio, Integer usuarioAutenticadoId,
                                             ETipoEquipamento tipoEquipamento, String nomeVariacao);

    ExercicioVariacaoResponse mapToExercicioVariacaoResponse(ExercicioVariacao exercicioVariacao);
}
