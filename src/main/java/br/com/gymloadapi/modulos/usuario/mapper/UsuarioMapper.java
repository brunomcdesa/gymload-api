package br.com.gymloadapi.modulos.usuario.mapper;

import br.com.gymloadapi.modulos.comum.enums.EAcao;
import br.com.gymloadapi.modulos.comum.types.Email;
import br.com.gymloadapi.modulos.usuario.dto.UsuarioRequest;
import br.com.gymloadapi.modulos.usuario.dto.UsuarioResponse;
import br.com.gymloadapi.modulos.usuario.enums.EUserRole;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import br.com.gymloadapi.modulos.usuario.model.UsuarioHistorico;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UUID.class, LocalDateTime.class})
public interface UsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "imagemPerfil", ignore = true)
    @Mapping(target = "senha", source = "encryptedPassword")
    @Mapping(target = "uuid", expression = "java(UUID.randomUUID())")
    Usuario mapToModel(UsuarioRequest usuarioRequest, String encryptedPassword, List<EUserRole> roles, Email email);

    @Mapping(target = "email", source = "usuario.email.valor")
    UsuarioResponse mapModelToResponse(Usuario usuario);

    @BeanMapping(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @Mapping(target = "email", source = "email")
    void editar(UsuarioRequest usuarioRequest, Email email, @MappingTarget Usuario usuario);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "acao", source = "acao")
    @Mapping(target = "usuarioCadastroId", source = "usuarioId")
    @Mapping(target = "dataCadastro", expression = "java(LocalDateTime.now())")
    UsuarioHistorico mapToHistorico(Usuario usuario, Integer usuarioId, EAcao acao);
}
