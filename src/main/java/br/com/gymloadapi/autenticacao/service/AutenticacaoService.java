package br.com.gymloadapi.autenticacao.service;

import br.com.gymloadapi.autenticacao.dto.CadastroRequest;
import br.com.gymloadapi.autenticacao.dto.LoginRequest;
import br.com.gymloadapi.autenticacao.dto.LoginResponse;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import br.com.gymloadapi.modulos.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutenticacaoService implements UserDetailsService {

    private final TokenService tokenService;
    private final UsuarioService usuarioService;
    private final AuthenticationConfiguration autenticacaoConfiguration;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioService.findByUsername(username);
    }

    @SneakyThrows
    public LoginResponse login(LoginRequest loginRequest) {
        var authenticationManager = autenticacaoConfiguration.getAuthenticationManager();
        var usernamePassword = new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());
        var auth = authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((Usuario) auth.getPrincipal());

        return new LoginResponse(token);
    }

    public void cadastrarUsuario(CadastroRequest cadastroRequest) {
        usuarioService.cadastrar(cadastroRequest);
    }

    public Usuario getUsuarioAutenticado() {
        var userDetails = this.getAuthentication();

        if (!(userDetails instanceof Usuario)) {
            throw new RuntimeException("Usuário autenticado não é uma instância de Usuario");
        }

        return (Usuario) userDetails;
    }

    private UserDetails getAuthentication() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            throw new RuntimeException("Usuário não autenticado");
        }

        return (UserDetails) authentication.getPrincipal();
    }
}
