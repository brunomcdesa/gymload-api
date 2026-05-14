package br.com.gymloadapi.modulos.treino.mapper;

import br.com.gymloadapi.modulos.comum.enums.EAcao;
import br.com.gymloadapi.modulos.treino.model.TreinoSessao;
import br.com.gymloadapi.modulos.treino.model.TreinoSessaoHistorico;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface TreinoSessaoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "acao", source = "acao")
    @Mapping(target = "usuarioCadastroId", source = "usuarioId")
    @Mapping(target = "dataCadastro", expression = "java(LocalDateTime.now())")
    TreinoSessaoHistorico mapToHistorico(TreinoSessao treinoSessao, Integer usuarioId, EAcao acao);
}
