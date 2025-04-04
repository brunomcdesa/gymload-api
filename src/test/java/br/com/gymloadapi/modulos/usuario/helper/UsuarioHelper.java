package br.com.gymloadapi.modulos.usuario.helper;

import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.experimental.UtilityClass;

import java.util.UUID;

import static br.com.gymloadapi.modulos.comum.utils.RolesUtils.ROLES_ADMIN;

@UtilityClass
public class UsuarioHelper {

    public static Usuario umUsuarioAdmin() {
        return Usuario.builder()
            .id(UUID.fromString("c2d83d78-e1b2-4f7f-b79d-1b83f3c435f9"))
            .nome("Usuario Admin")
            .roles(ROLES_ADMIN)
            .username("usuarioAdmin")
            .senha("123456")
            .build();
    }
}
