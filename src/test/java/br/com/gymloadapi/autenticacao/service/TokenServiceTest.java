package br.com.gymloadapi.autenticacao.service;

import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;

import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuario;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "secret", "teste-secret-key-para-jwt");
    }

    @Test
    void generateToken_deveRetornarToken_quandoUsuarioValido() {
        var usuario = umUsuario();

        var token = service.generateToken(usuario);
        var decodedJWT = JWT.decode(token);

        assertAll(
            () -> assertNotNull(token),
            () -> assertFalse(token.isEmpty()),
            () -> assertEquals("usuarioUser", decodedJWT.getSubject()),
            () -> assertEquals("Usuario", decodedJWT.getClaim("usuarioNome").asString()),
            () -> assertArrayEquals(new String[]{"ROLE_USER"}, decodedJWT.getClaim("usuarioRoles").asArray(String.class)),
            () -> assertNotNull(decodedJWT.getExpiresAt())
        );
    }

    @Test
    void generateToken_deveLancarValidacaoException_quandoOcorreErro() {
        var usuario = umUsuario();

        try (var jwtMockedStatic = mockStatic(JWT.class)) {
            var jwtBuilder = mock(Builder.class);

            jwtMockedStatic.when(JWT::create).thenReturn(jwtBuilder);
            when(jwtBuilder.withIssuer(anyString())).thenReturn(jwtBuilder);
            when(jwtBuilder.withSubject(anyString())).thenReturn(jwtBuilder);
            when(jwtBuilder.withClaim(anyString(), anyString())).thenReturn(jwtBuilder);
            when(jwtBuilder.withArrayClaim(anyString(), any(String[].class))).thenReturn(jwtBuilder);
            when(jwtBuilder.withExpiresAt(any(Instant.class))).thenReturn(jwtBuilder);
            when(jwtBuilder.sign(any())).thenThrow(new JWTCreationException("Erro ao criar token", new Exception()));

            var exception = assertThrows(
                ValidacaoException.class,
                () -> service.generateToken(usuario)
            );
            assertEquals("Erro ao gerar token.", exception.getMessage());
        }
    }

    @Test
    void validateToken_deveRetornarUsername_quandoTokenValido() {
        var token = service.generateToken(umUsuario());
        assertEquals("usuarioUser", service.validateToken(token));
    }

    @Test
    void validateToken_deveLancarException_quandoTokenInvalido() {
        var exception = assertThrows(
            BadCredentialsException.class,
            () -> service.validateToken("token-invalido")
        );
        assertEquals("Token inválido.", exception.getMessage());
    }
}
