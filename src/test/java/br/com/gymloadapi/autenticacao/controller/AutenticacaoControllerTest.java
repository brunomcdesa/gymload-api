package br.com.gymloadapi.autenticacao.controller;

import br.com.gymloadapi.autenticacao.dto.LoginRequest;
import br.com.gymloadapi.autenticacao.service.AutenticacaoService;
import br.com.gymloadapi.autenticacao.service.TokenService;
import br.com.gymloadapi.config.security.JwtAccessDeinedHandler;
import br.com.gymloadapi.config.security.SecurityConfiguration;
import br.com.gymloadapi.modulos.comum.service.BackBlazeService;
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

import static br.com.gymloadapi.autenticacao.helper.AutenticacaoHelper.umLoginAdminRequest;
import static br.com.gymloadapi.helper.TestsHelper.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@WithAnonymousUser
@MockitoBean(types = {UsuarioService.class, BackBlazeService.class})
@WebMvcTest(AutenticacaoController.class)
@Import({SecurityConfiguration.class, TokenService.class, BackBlazeService.class, JwtAccessDeinedHandler.class})
class AutenticacaoControllerTest {

    private static final String URL = "/auth";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AutenticacaoService service;

    @ParameterizedTest
    @CsvSource(value = {"NULL,NULL", "'',''", "'   ', '   '"}, nullValues = {"NULL"})
    void login_deveRetornarBadRequest_quandoCamposObrigatoriosInvalidos(String userName, String password) {
        var request = new LoginRequest(userName, password);
        isBadRequest(post(URL + "/login"), mockMvc, request,
            "O campo username é obrigatório.",
            "O campo password é obrigatório."
        );

        verifyNoInteractions(service);
    }

    @Test
    void login_deveRetornarOk_quandoCamposObrigatoriosValidos() {
        var request = umLoginAdminRequest();
        isOk(post(URL + "/login"), mockMvc, request);

        verify(service).login(request);
    }

    @ParameterizedTest
    @CsvSource(value = {"NULL,NULL", "'',''", "'   ', '   '"}, nullValues = {"NULL"})
    void alterarSenha_deveRetornarBadRequest_quandoCamposObrigatoriosInvalidos(String userName, String password) {
        var request = new LoginRequest(userName, password);
        isBadRequest(put(URL + "/alterar-senha"), mockMvc, request,
            "O campo username é obrigatório.",
            "O campo password é obrigatório."
        );

        verifyNoInteractions(service);
    }

    @Test
    void alterarSenha_deveRetornarNoContent_quandoCamposObrigatoriosValidos() {
        var request = umLoginAdminRequest();
        isNoContent(put(URL + "/alterar-senha"), mockMvc, request);

        verify(service).alterarSenha(request);
    }
}
