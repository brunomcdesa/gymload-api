package br.com.gymloadapi.modulos.treino.mapper;

import br.com.gymloadapi.modulos.comum.enums.EAcao;
import br.com.gymloadapi.modulos.comum.enums.ESituacao;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioResponse;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.treino.dto.TreinoCompartilhadoResponse;
import br.com.gymloadapi.modulos.treino.dto.TreinoRequest;
import br.com.gymloadapi.modulos.treino.dto.TreinoResponse;
import br.com.gymloadapi.modulos.treino.model.Treino;
import br.com.gymloadapi.modulos.treino.model.TreinoCompartilhamento;
import br.com.gymloadapi.modulos.treino.model.TreinoHistorico;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", imports = {LocalDate.class, LocalDateTime.class, ESituacao.class})
public interface TreinoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nome", source = "request.nome")
    @Mapping(target = "usuario", source = "usuario")
    @Mapping(target = "exercicios", source = "exercicios")
    @Mapping(target = "dataCadastro", expression = "java(LocalDate.now())")
    @Mapping(target = "situacao", expression = "java(ESituacao.ATIVO)")
    @Mapping(target = "importado", constant = "false")
    Treino mapToModel(TreinoRequest request, Usuario usuario, List<Exercicio> exercicios);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "usuario", source = "usuario")
    @Mapping(target = "exercicios", source = "exercicios")
    @Mapping(target = "dataCadastro", expression = "java(LocalDate.now())")
    @Mapping(target = "situacao", expression = "java(ESituacao.ATIVO)")
    @Mapping(target = "importado", constant = "true")
    Treino mapCompartilhadoToModel(String nome, Usuario usuario, List<Exercicio> exercicios);

    TreinoResponse mapToResponse(Treino treino);

    @Mapping(target = "nomeTreino", source = "compartilhamento.nomeTreino")
    @Mapping(target = "dataExpiracao",
        expression = "java(compartilhamento.getDataExpiracao().toLocalDate())")
    TreinoCompartilhadoResponse mapToCompartilhadoResponse(
        TreinoCompartilhamento compartilhamento, List<ExercicioResponse> exercicios);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "acao", source = "acao")
    @Mapping(target = "usuarioCadastroId", source = "usuarioId")
    @Mapping(target = "dataCadastro", expression = "java(LocalDateTime.now())")
    TreinoHistorico mapToHistorico(Treino treino, Integer usuarioId, EAcao acao);
}
