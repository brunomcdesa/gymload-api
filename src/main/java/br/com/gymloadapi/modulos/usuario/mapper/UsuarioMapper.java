package br.com.gymloadapi.modulos.usuario.mapper;

import br.com.gymloadapi.modulos.usuario.dto.UsuarioRequest;
import br.com.gymloadapi.modulos.usuario.dto.UsuarioResponse;
import br.com.gymloadapi.modulos.usuario.enums.EUserRole;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface UsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "senha", source = "encryptedPassword")
    @Mapping(target = "roles", source = "roles")
    Usuario mapToModel(UsuarioRequest usuarioRequest, String encryptedPassword, List<EUserRole> roles);

    UsuarioResponse mapModelToResponse(Usuario usuario);
}
