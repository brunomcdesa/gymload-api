package br.com.gymloadapi.autenticacao.service;

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

import static br.com.gymloadapi.autenticacao.helper.AutenticacaoHelper.umLoginRequest;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
}
