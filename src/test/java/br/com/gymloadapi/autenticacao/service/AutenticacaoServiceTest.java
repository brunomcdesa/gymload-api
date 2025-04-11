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

import static br.com.gymloadapi.autenticacao.helper.AutenticacaoHelper.umLoginAdminRequest;
import static br.com.gymloadapi.autenticacao.helper.AutenticacaoHelper.umLoginUserRequest;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuario;
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

        assertEquals("token-jwt-valido", service.login(umLoginAdminRequest()).token());

        verify(authenticationConfiguration).getAuthenticationManager();
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(authentication).getPrincipal();
        verify(tokenService).generateToken(usuario);
    }

    @Test
    @SneakyThrows
    void login_deveLancarException_quandoAutenticacaoFalhar() {
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new RuntimeException("Falha na autenticação"));

        var exception = assertThrowsExactly(
            ValidacaoException.class,
            () -> service.login(umLoginAdminRequest())
        );
        assertEquals("Erro ao realizar login. Username ou senha inválidos.", exception.getMessage());

        verify(authenticationConfiguration).getAuthenticationManager();
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(tokenService);
    }

    @Test
    void alterarSenha_deveLancarException_quandoNovaSenhaIgualAAnterior() {
        when(usuarioService.findByUsername("usuarioUser")).thenReturn(umUsuario());

        var exception = assertThrowsExactly(
            ValidacaoException.class,
            () -> service.alterarSenha(umLoginUserRequest("123456"))
        );
        assertEquals("A senha deve ser diferente da senha anterior.", exception.getMessage());

        verify(usuarioService).findByUsername("usuarioUser");
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    void alterarSenha_naoDeveLancarException_quandoNovaSenhaDiferenteDaAnterior() {
        when(usuarioService.findByUsername("usuarioUser")).thenReturn(umUsuario());

        assertDoesNotThrow(() -> service.alterarSenha(umLoginUserRequest("654321")));

        verify(usuarioService).findByUsername("usuarioUser");
        verify(usuarioService).atualizarSenha(eq("usuarioUser"), any(String.class));
    }
}
