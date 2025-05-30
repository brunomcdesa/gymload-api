package br.com.gymloadapi.modulos.usuario.mapper;

import br.com.gymloadapi.modulos.usuario.dto.UsuarioRequest;
import br.com.gymloadapi.modulos.usuario.dto.UsuarioResponse;
import br.com.gymloadapi.modulos.usuario.enums.EUserRole;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = UUID.class)
public interface UsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "senha", source = "encryptedPassword")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "uuid", expression = "java(UUID.randomUUID())")
    Usuario mapToModel(UsuarioRequest usuarioRequest, String encryptedPassword, List<EUserRole> roles);

    UsuarioResponse mapModelToResponse(Usuario usuario);

    @BeanMapping(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
    void editar(UsuarioRequest usuarioRequest, @MappingTarget Usuario usuario);
}
