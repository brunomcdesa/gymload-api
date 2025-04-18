package br.com.gymloadapi.modulos.usuario.helper;

import br.com.gymloadapi.modulos.usuario.dto.UsuarioRequest;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.UUID;

import static br.com.gymloadapi.modulos.comum.utils.PasswordUtils.encodePassword;
import static br.com.gymloadapi.modulos.comum.utils.RolesUtils.ROLES_ADMIN;
import static br.com.gymloadapi.modulos.comum.utils.RolesUtils.ROLES_USER;

@UtilityClass
public class UsuarioHelper {

    public static final UUID USUARIO_ADMIN_UUID = UUID.fromString("c2d83d78-e1b2-4f7f-b79d-1b83f3c435f9");

    public static UsuarioRequest umUsuarioRequest() {
        return new UsuarioRequest("Usuario", "usuario", "123456");
    }

    public static UsuarioRequest umUsuarioAdminRequest() {
        return new UsuarioRequest("Usuario Admin", "usuarioAdmin", "654321");
    }

    public static UsuarioRequest umUsuarioRequestSemSenha() {
        return new UsuarioRequest("Usuario Edicao", "usernameEdicao", null);
    }

    public static Usuario umUsuarioAdmin() {
        return Usuario.builder()
            .id(1)
            .uuid(USUARIO_ADMIN_UUID)
            .nome("Usuario Admin")
            .roles(ROLES_ADMIN)
            .username("usuarioAdmin")
            .senha(encodePassword("654321"))
            .build();
    }

    public static Usuario umUsuario() {
        return Usuario.builder()
            .id(2)
            .uuid(UUID.fromString("8689ea4e-3a85-4b6b-80f2-fc04f3cdd712"))
            .nome("Usuario")
            .roles(ROLES_USER)
            .username("usuarioUser")
            .imagemPerfil("802421c7-f8fd-454e-ab59-9ea346a2a444-Usuario.png")
            .senha(encodePassword("123456"))
            .build();
    }

    public static Usuario outroUsuario() {
        return Usuario.builder()
            .id(3)
            .uuid(UUID.fromString("802421c7-f8fd-454e-ab59-9ea346a2a444"))
            .nome("Usuario 2")
            .roles(ROLES_USER)
            .username("usuarioUser2")
            .imagemPerfil("802421c7-f8fd-454e-ab59-9ea346a2a444-Usuario 2.png")
            .senha(encodePassword("123456"))
            .build();
    }

    public static List<Usuario> umaListaDeUsuarios() {
        return List.of(umUsuario(), umUsuarioAdmin());
    }
}
