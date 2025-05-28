package br.com.gymloadapi.modulos.usuario.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuario;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;
import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void isAdmin_deveRetornarTrue_quandoUsuarioForAdmin() {
        assertTrue(umUsuarioAdmin().isAdmin());
    }

    @Test
    void isAdmin_deveRetornarFalse_quandoUsuarioNaoForAdmin() {
        assertFalse(umUsuario().isAdmin());
    }

    @Test
    void alterarSenha_deveAlterarSenhaDoUsuario_quandoSolicitado() {
        var usuario = umUsuario();

        usuario.alterarSenha("nova-senha");

        assertEquals("nova-senha", usuario.getSenha());
    }
}
