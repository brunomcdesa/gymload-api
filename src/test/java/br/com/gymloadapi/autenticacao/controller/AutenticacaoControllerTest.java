package br.com.gymloadapi.autenticacao.controller;

import br.com.gymloadapi.autenticacao.dto.LoginRequest;
import br.com.gymloadapi.autenticacao.service.AutenticacaoService;
import br.com.gymloadapi.autenticacao.service.TokenService;
import br.com.gymloadapi.config.security.JwtAccessDeinedHandler;
import br.com.gymloadapi.config.security.SecurityConfiguration;
import br.com.gymloadapi.modulos.usuario.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static br.com.gymloadapi.autenticacao.helper.AutenticacaoHelper.umLoginRequest;
import static br.com.gymloadapi.helper.TestsHelper.isBadRequest;
import static br.com.gymloadapi.helper.TestsHelper.isOk;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WithAnonymousUser
@MockitoBean(types = UsuarioService.class)
@WebMvcTest(AutenticacaoController.class)
@Import({SecurityConfiguration.class, TokenService.class, JwtAccessDeinedHandler.class})
class AutenticacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AutenticacaoService service;

    @ParameterizedTest
    @CsvSource(value = {"NULL,NULL", "'',''", "'   ', '   '"}, nullValues = {"NULL"})
    void login_deveRetornarBadRequest_quandoCamposObrigatoriosInvalidos(String userName, String password) {
        var request = new LoginRequest(userName, password);
        isBadRequest(post("/auth/login"), mockMvc, request,
            "O campo username é obrigatório.",
            "O campo password é obrigatório."
        );

        verifyNoInteractions(service);
    }

    @Test
    void login_deveRetornarOk_quandoCamposObrigatoriosValidos() {
        var request = umLoginRequest();
        isOk(post("/auth/login"), mockMvc, request);

        verify(service).login(request);
    }
}
