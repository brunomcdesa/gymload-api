package br.com.gymloadapi.modulos.usuario.controller;

import br.com.gymloadapi.autenticacao.service.TokenService;
import br.com.gymloadapi.config.TestSecurityConfiguration;
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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static br.com.gymloadapi.helper.TestsHelper.*;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(UsuarioController.class)
@Import({SecurityConfiguration.class, TokenService.class, JwtAccessDeinedHandler.class, TestSecurityConfiguration.class})
class UsuarioControllerTest {

    private static final String URL = "/api/usuarios";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UsuarioService service;

    @WithAnonymousUser
    @ParameterizedTest
    @ValueSource(strings = {"", "/imagem-perfil"})
    void gets_devemRetorarUnauthorized_quandoUsuarioNaoAutenticado(String endpoint) {
        isUnauthorized(get(URL + endpoint), mockMvc);
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
        isBadRequestMultipart(POST, URL + "/cadastro", mockMvc, umMockMultipartFile(), "usuarioRequest", request,
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
        var file = umMockMultipartFile();
        isCreatedMultipart(POST, URL + "/cadastro", mockMvc, file, "usuarioRequest", request);

        verify(service).cadastrar(request, false, file);
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

        verify(service).cadastrar(request, true, null);
    }

    @Test
    @WithAnonymousUser
    void editar_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        isUnauthorized(put(URL + "/c2d83d78-e1b2-4f7f-b79d-1b83f3c435f9/editar"), mockMvc);
        verifyNoInteractions(service);
    }

    @WithUserDetails
    @ParameterizedTest
    @CsvSource(value = {"NULL,NULL,'123'", "'','',''", "'  ','  ','  '"}, nullValues = "NULL")
    void editar_deveRetornarBadRequest_quandoCamposObrigatoriosInvalidos(String nome, String username, String password) {
        var request = new UsuarioRequest(nome, username, password);
        isBadRequestMultipart(PUT, URL + "/c2d83d78-e1b2-4f7f-b79d-1b83f3c435f9/editar",
            mockMvc, umMockMultipartFile(), "usuarioRequest", request,
            "O campo nome é obrigatório.",
            "O campo username é obrigatório.",
            "O campo password deve ser nulo");

        verifyNoInteractions(service);
    }

    @Test
    @WithUserDetails
    void editar_deveRetornarNoContent_quandoCamposObrigatoriosValidos() {
        var request = umUsuarioRequestSemSenha();
        var file = umMockMultipartFile();
        isNoContentMultipart(PUT, URL + "/c2d83d78-e1b2-4f7f-b79d-1b83f3c435f9/editar", mockMvc,
            file, "usuarioRequest", request);

        verify(service).editar(USUARIO_ADMIN_UUID, request, file, umUsuarioAdmin());
    }

    @Test
    @WithUserDetails
    void buscarImagemPerfil_deveRetornarOk_quandoUsuarioAutenticado() {
        isOk(get(URL + "/imagem-perfil"), mockMvc);

        verify(service).buscarImagemPerfil(umUsuarioAdmin());
    }
}
