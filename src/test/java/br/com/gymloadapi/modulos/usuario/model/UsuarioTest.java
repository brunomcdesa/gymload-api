package br.com.gymloadapi.modulos.usuario.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuario;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioTest {

    @Test
    void getAuthorities_deveRetornarAuthoritiesDoUsuario_quandoSolicitado() {
        assertTrue(umUsuario().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void getPassword_deveRetornarASenha_quandoSolicitado() {
        assertTrue(umUsuario().getPassword().startsWith("$2a$"));
    }

    @Test
    void isAccountNonExpired_deveRetornarTrue_quandoSolicitado() {
        assertTrue(umUsuario().isAccountNonExpired());
    }

    @Test
    void isAccountNonLocked_deveRetornarTrue_quandoSolicitado() {
        assertTrue(umUsuario().isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpired_deveRetornarTrue_quandoSolicitado() {
        assertTrue(umUsuario().isCredentialsNonExpired());
    }

    @Test
    void isEnabled_deveRetornarTrue_quandoSolicitado() {
        assertTrue(umUsuario().isEnabled());
    }

    @Test
    void getRolesArray_deveRetornarTrue_quandoSolicitado() {
        assertArrayEquals(new String[]{"ROLE_USER"}, umUsuario().getRolesArray());
    }
}
