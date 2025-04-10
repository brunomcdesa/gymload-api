package br.com.gymloadapi.modulos.treino.mapper;

import br.com.gymloadapi.modulos.comum.enums.EAcao;
import br.com.gymloadapi.modulos.comum.enums.ESituacao;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.treino.dto.TreinoRequest;
import br.com.gymloadapi.modulos.treino.dto.TreinoResponse;
import br.com.gymloadapi.modulos.treino.model.Treino;
import br.com.gymloadapi.modulos.treino.model.TreinoHistorico;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(imports = {LocalDate.class, LocalDateTime.class, ESituacao.class})
public interface TreinoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nome", source = "request.nome")
    @Mapping(target = "usuario", source = "usuario")
    @Mapping(target = "exercicios", source = "exercicios")
    @Mapping(target = "dataCadastro", expression = "java(LocalDate.now())")
    @Mapping(target = "situacao", expression = "java(ESituacao.ATIVO)")
    Treino mapToModel(TreinoRequest request, Usuario usuario, List<Exercicio> exercicios);

    TreinoResponse mapToResponse(Treino treino);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "acao", source = "acao")
    @Mapping(target = "usuarioCadastroId", source = "usuarioId")
    @Mapping(target = "dataCadastro", expression = "java(LocalDateTime.now())")
    TreinoHistorico mapToHistorico(Treino treino, Integer usuarioId, EAcao acao);
}
