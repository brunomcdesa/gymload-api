package br.com.gymloadapi.modulos.usuario.helper;

import br.com.gymloadapi.modulos.usuario.dto.UsuarioRequest;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.UUID;

import static br.com.gymloadapi.modulos.comum.utils.RolesUtils.ROLES_ADMIN;
import static br.com.gymloadapi.modulos.comum.utils.RolesUtils.ROLES_USER;

@UtilityClass
public class UsuarioHelper {

    public static UsuarioRequest umUsuarioRequest() {
        return new UsuarioRequest("Usuario", "usuario", "123456");
    }

    public static UsuarioRequest umUsuarioAdminRequest() {
        return new UsuarioRequest("Usuario Admin", "usuarioAdmin", "654321");
    }

    public static Usuario umUsuarioAdmin() {
        return Usuario.builder()
            .id(UUID.fromString("c2d83d78-e1b2-4f7f-b79d-1b83f3c435f9"))
            .nome("Usuario Admin")
            .roles(ROLES_ADMIN)
            .username("usuarioAdmin")
            .senha(new BCryptPasswordEncoder().encode("654321"))
            .build();
    }

    public static Usuario umUsuario() {
        return Usuario.builder()
            .id(UUID.fromString("8689ea4e-3a85-4b6b-80f2-fc04f3cdd712"))
            .nome("Usuario")
            .roles(ROLES_USER)
            .username("usuarioUser")
            .senha(new BCryptPasswordEncoder().encode("123456"))
            .build();
    }

    public static List<Usuario> umaListaDeUsuarios() {
        return List.of(umUsuario(), umUsuarioAdmin());
    }
}
