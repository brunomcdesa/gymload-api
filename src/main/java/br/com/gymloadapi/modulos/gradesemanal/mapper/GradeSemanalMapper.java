package br.com.gymloadapi.modulos.gradesemanal.mapper;

import br.com.gymloadapi.modulos.comum.enums.EAcao;
import br.com.gymloadapi.modulos.comum.enums.EDiaSemana;
import br.com.gymloadapi.modulos.gradesemanal.dto.GradeSemanalHojeResponse;
import br.com.gymloadapi.modulos.gradesemanal.dto.GradeSemanalResponse;
import br.com.gymloadapi.modulos.gradesemanal.model.GradeSemanal;
import br.com.gymloadapi.modulos.gradesemanal.model.GradeSemanalHistorico;
import br.com.gymloadapi.modulos.treino.model.Treino;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = LocalDateTime.class)
public interface GradeSemanalMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "diaSemana", source = "diaSemana")
    @Mapping(target = "treino", source = "treino")
    @Mapping(target = "usuario", source = "usuario")
    GradeSemanal mapToModel(EDiaSemana diaSemana, Treino treino, Usuario usuario);

    @Mapping(target = "diaSemana", source = "diaSemana")
    @Mapping(target = "treinoId", source = "treino.id")
    @Mapping(target = "treinoNome", source = "treino.nome")
    GradeSemanalResponse mapToResponse(GradeSemanal gradeSemanal);

    @Mapping(target = "treinoId", source = "gradeSemanal.treino.id")
    @Mapping(target = "treinoNome", source = "gradeSemanal.treino.nome")
    @Mapping(target = "concluidoHoje", source = "concluidoHoje")
    GradeSemanalHojeResponse mapToHojeResponse(GradeSemanal gradeSemanal, boolean concluidoHoje);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "acao", source = "acao")
    @Mapping(target = "usuarioCadastroId", source = "usuarioId")
    @Mapping(target = "dataCadastro", expression = "java(LocalDateTime.now())")
    @Mapping(target = "diaSemana", source = "gradeSemanal.diaSemana")
    @Mapping(target = "treino", source = "gradeSemanal.treino")
    @Mapping(target = "usuario", source = "gradeSemanal.usuario")
    GradeSemanalHistorico mapToHistorico(GradeSemanal gradeSemanal, Integer usuarioId, EAcao acao);
}
