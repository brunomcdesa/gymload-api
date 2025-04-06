package br.com.gymloadapi.modulos.usuario.controller;

import br.com.gymloadapi.autenticacao.service.TokenService;
import br.com.gymloadapi.config.security.JwtAccessDeinedHandler;
import br.com.gymloadapi.config.security.SecurityConfiguration;
import br.com.gymloadapi.modulos.usuario.dto.UsuarioRequest;
import br.com.gymloadapi.modulos.usuario.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static br.com.gymloadapi.helper.TestsHelper.*;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdminRequest;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioRequest;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(UsuarioController.class)
@Import({SecurityConfiguration.class, TokenService.class, JwtAccessDeinedHandler.class})
class UsuarioControllerTest {

    private static final String URL = "/api/usuarios";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UsuarioService service;

    @Test
    @WithAnonymousUser
    void buscarTodos_deveRetorarUnauthorized_quandoUsuarioNaoAutenticado() {
        isUnauthorized(get(URL), mockMvc);
        verifyNoInteractions(service);
    }

    @Test
    @WithMockUser
    void buscarTodos_deveRetorarForbidden_quandoUsuarioNaoForAdmin() {
        isForbidden(get(URL), mockMvc);
        verifyNoInteractions(service);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void buscarTodos_deveRetorarOk_quandoUsuarioForAdmin() {
        isOk(get(URL), mockMvc);
        verify(service).buscarTodos();
    }

    @ParameterizedTest
    @WithAnonymousUser
    @ValueSource(strings = {"/cadastro", "/cadastro/admin"})
    void posts_devemRetornarUnauthorized_quandoUsuarioNaoAutenticado(String endpoint) {
        isUnauthorized(post(endpoint), mockMvc);
        verifyNoInteractions(service);
    }

    @WithMockUser
    @ParameterizedTest
    @CsvSource(value = {"NULL,NULL,NULL", "'','',''", "'  ','  ','  '"}, nullValues = {"NULL"})
    void cadastrar_deveRetornarBadRequest_quandoCamposObrigatoriosInvalidos(String nome, String username, String password) {
        var request = new UsuarioRequest(nome, username, password);
        isBadRequest(post(URL + "/cadastro"), mockMvc, request,
            "O campo nome é obrigatório.",
            "O campo username é obrigatório.",
            "O campo password é obrigatório."
        );
        verifyNoInteractions(service);
    }

    @Test
    @WithMockUser
    void cadastrar_deveRetornarCreated_quandoCamposObrigatoriosValidos() {
        var request = umUsuarioRequest();
        isCreated(post(URL + "/cadastro"), mockMvc, request);

        verify(service).cadastrar(request, false);
    }

    @Test
    @WithMockUser
    void cadastrarAdmin_deveRetornarForbidden_quandoUsaurioNaoForAdmin() {
        isForbidden(post(URL + "/cadastro/admin"), mockMvc);
        verifyNoInteractions(service);
    }

    @ParameterizedTest
    @WithMockUser(roles = "ADMIN")
    @CsvSource(value = {"NULL,NULL,NULL", "'','',''", "'  ','  ','  '"}, nullValues = {"NULL"})
    void cadastrarAdmin_deveRetornarBadRequest_quandoCamposObrigatoriosInvalidos(String nome, String username, String password) {
        var request = new UsuarioRequest(nome, username, password);
        isBadRequest(post(URL + "/cadastro/admin"), mockMvc, request,
            "O campo nome é obrigatório.",
            "O campo username é obrigatório.",
            "O campo password é obrigatório."
        );
        verifyNoInteractions(service);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void cadastrarAdmin_deveRetornarCreated_quandoCamposObrigatoriosValidos() {
        var request = umUsuarioAdminRequest();
        isCreated(post(URL + "/cadastro/admin"), mockMvc, request);

        verify(service).cadastrar(request, true);
    }
}
