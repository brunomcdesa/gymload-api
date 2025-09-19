package br.com.gymloadapi.modulos.tipoVariacao.mapper;

import br.com.gymloadapi.modulos.comum.dto.SelectResponse;
import br.com.gymloadapi.modulos.comum.enums.EAcao;
import br.com.gymloadapi.modulos.tipoVariacao.dto.TipoVariacaoRequest;
import br.com.gymloadapi.modulos.tipoVariacao.dto.TipoVariacaoResponse;
import br.com.gymloadapi.modulos.tipoVariacao.model.TipoVariacao;
import br.com.gymloadapi.modulos.tipoVariacao.model.TipoVariacaoHistorico;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import org.mapstruct.*;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface TipoVariacaoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nome", source = "request.nome")
    @Mapping(target = "dataCadastro", expression = "java(LocalDateTime.now())")
    @Mapping(target = "usuarioCadastroId", source = "usuarioAutenticado.id")
    @Mapping(target = "usuarioCadastroNome", source = "usuarioAutenticado.nome")
    TipoVariacao mapToModel(TipoVariacaoRequest request, Usuario usuarioAutenticado);

    TipoVariacaoResponse mapToResponse(TipoVariacao tipoVariacao);

    @Mapping(target = "value", source = "tipoVariacao.id")
    @Mapping(target = "label", source = "tipoVariacao.nome")
    SelectResponse mapToSelectResponse(TipoVariacao tipoVariacao);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "acao", source = "acao")
    @Mapping(target = "tipoVariacao", source = "tipoVariacao")
    @Mapping(target = "dataCadastro", expression = "java(LocalDateTime.now())")
    @Mapping(target = "usuarioCadastroId", source = "usuarioId")
    TipoVariacaoHistorico mapToHistorico(TipoVariacao tipoVariacao, Integer usuarioId, EAcao acao);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nome", source = "request.nome")
    @BeanMapping(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
    void editarTipoVariacao(TipoVariacaoRequest request, @MappingTarget TipoVariacao tipoVariacao);
}
