package br.com.gymloadapi.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;

import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;

@TestConfiguration
public class TestSecurityConfiguration {

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> umUsuarioAdmin();
    }
}
