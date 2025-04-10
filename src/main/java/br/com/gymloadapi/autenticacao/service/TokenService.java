package br.com.gymloadapi.autenticacao.service;

import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    private static final String API_ISSUER = "gymload-api";

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(Usuario usuario) {
        try {
            return JWT.create()
                .withIssuer(API_ISSUER)
                .withSubject(usuario.getUsername())
                .withClaim("usuarioNome", usuario.getNome())
                .withArrayClaim("usuarioRoles", usuario.getRolesArray())
                .withExpiresAt(this.getExpirationDate())
                .sign(this.getAlgorithm());
        } catch (JWTCreationException exception) {
            throw new ValidacaoException("Erro ao gerar token.", exception);
        }
    }

    public String validateToken(String token) {
        try {
            return JWT.require(this.getAlgorithm())
                .withIssuer(API_ISSUER)
                .build()
                .verify(token)
                .getSubject();
        } catch (JWTVerificationException exception) {
            throw new BadCredentialsException("Token inválido.");
        }
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secret);
    }

    private Instant getExpirationDate() {
        return LocalDateTime.now()
            .plusHours(2)
            .toInstant(ZoneOffset.of("-03:00"));
    }
}
