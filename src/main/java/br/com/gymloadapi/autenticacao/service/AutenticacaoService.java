package br.com.gymloadapi.autenticacao.service;

import br.com.gymloadapi.autenticacao.dto.LoginRequest;
import br.com.gymloadapi.autenticacao.dto.LoginResponse;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import br.com.gymloadapi.modulos.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static br.com.gymloadapi.modulos.comum.utils.PasswordUtils.encodePassword;
import static br.com.gymloadapi.modulos.comum.utils.PasswordUtils.isPasswordEquals;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutenticacaoService implements UserDetailsService {

    private final TokenService tokenService;
    private final UsuarioService usuarioService;
    private final AuthenticationConfiguration autenticacaoConfiguration;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.findByUsername(username);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            var authenticationManager = autenticacaoConfiguration.getAuthenticationManager();
            var usernamePassword = new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());
            var auth = authenticationManager.authenticate(usernamePassword);

            var token = tokenService.generateToken((Usuario) auth.getPrincipal());

            return new LoginResponse(token);
        } catch (Exception exception) {
            log.error("Erro ao realizar login", exception);
            throw new ValidacaoException("Erro ao realizar login. Username ou senha inválidos.");
        }
    }

    public void alterarSenha(LoginRequest loginRequest) {
        var usuario = this.findByUsername(loginRequest.username());
        var novaSenha = loginRequest.password();

        if (isPasswordEquals(novaSenha, usuario.getPassword())) {
            throw new ValidacaoException("A senha deve ser diferente da senha anterior.");
        }

        usuarioService.atualizarSenha(usuario.getUsername(), encodePassword(novaSenha));
    }

    private UserDetails findByUsername(String username) {
        return usuarioService.findByUsername(username);
    }
}
