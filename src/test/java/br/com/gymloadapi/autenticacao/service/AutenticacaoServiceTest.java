package br.com.gymloadapi.autenticacao.service;

import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.usuario.service.UsuarioService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

import static br.com.gymloadapi.autenticacao.helper.AutenticacaoHelper.umLoginRequest;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

    @InjectMocks
    private AutenticacaoService service;
    @Mock
    private TokenService tokenService;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private AuthenticationConfiguration authenticationConfiguration;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @Test
    void loadUserByUsername_deveRetornarUserDetails_quandoSolicitado() {
        when(usuarioService.findByUsername("admin")).thenReturn(umUsuarioAdmin());

        var userDetails = service.loadUserByUsername("admin");
        assertAll(
            () -> assertEquals("usuarioAdmin", userDetails.getUsername()),
            () -> assertTrue(userDetails.getPassword().startsWith("$2a$"))
        );

        verify(usuarioService).findByUsername("admin");
    }

    @Test
    @SneakyThrows
    void login_deveRetornarLoginResponseComToken_quandoCredenciaisValidas() {
        var usuario = umUsuarioAdmin();

        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuario);
        when(tokenService.generateToken(usuario)).thenReturn("token-jwt-valido");

        assertEquals("token-jwt-valido", service.login(umLoginRequest()).token());

        verify(authenticationConfiguration).getAuthenticationManager();
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(authentication).getPrincipal();
        verify(tokenService).generateToken(usuario);
    }

    @Test
    void getUsuarioAutenticado_deveRetornarUsuario_quandoUsuarioEstaAutenticado() {
        var usuario = umUsuarioAdmin();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuario);

        var resultado = service.getUsuarioAutenticado();

        assertAll(
            () -> assertEquals("c2d83d78-e1b2-4f7f-b79d-1b83f3c435f9", resultado.getId().toString()),
            () -> assertEquals("Usuario Admin", resultado.getNome())
        );

        verify(securityContext).getAuthentication();
        verify(authentication, times(2)).getPrincipal();
    }

    @Test
    void getUsuarioAutenticado_deveLancarException_quandoUsuarioNaoEstiverAutenticado() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);

        var exception = assertThrows(
            ValidacaoException.class,
            () -> service.getUsuarioAutenticado()
        );
        assertEquals("Usuário não autenticado", exception.getMessage());

        verify(securityContext).getAuthentication();
        verifyNoInteractions(authentication);
    }

    @Test
    void getUsuarioAutenticado_deveLancarExceprion_quandoPrincipalNaoForUserDetails() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("String não é UserDetails");

        var exception = assertThrowsExactly(
            ValidacaoException.class,
            () -> service.getUsuarioAutenticado()
        );
        assertEquals("Usuário não autenticado", exception.getMessage());

        verify(securityContext).getAuthentication();
        verify(authentication).getPrincipal();
    }

    @Test
    void getUsuarioAutenticado_deveLancarException_quandoPrincipalNaoForUsuario() {
        var userDetailsMock = User.builder()
            .username("usuario")
            .password("senha")
            .authorities(Collections.emptyList())
            .build();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetailsMock);

        var exception = assertThrowsExactly(
            ValidacaoException.class,
            () -> service.getUsuarioAutenticado()
        );
        assertEquals("Usuário autenticado não é uma instância de Usuario", exception.getMessage());

        verify(securityContext).getAuthentication();
        verify(authentication, times(2)).getPrincipal();
    }
}
