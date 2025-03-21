package br.com.gymloadapi.modulos.usuario.mapper;

import br.com.gymloadapi.modulos.usuario.dto.UsuarioResponse;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import org.mapstruct.Mapper;

@Mapper
public interface UsuarioMapper {

    UsuarioResponse mapModelToResponse(Usuario usuario);
}
