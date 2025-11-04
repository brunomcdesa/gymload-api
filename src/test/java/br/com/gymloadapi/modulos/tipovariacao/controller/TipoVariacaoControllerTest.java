package br.com.gymloadapi.modulos.tipovariacao.controller;

import br.com.gymloadapi.autenticacao.service.TokenService;
import br.com.gymloadapi.config.TestSecurityConfiguration;
import br.com.gymloadapi.config.security.JwtAccessDeinedHandler;
import br.com.gymloadapi.config.security.SecurityConfiguration;
import br.com.gymloadapi.modulos.comum.service.BackBlazeService;
import br.com.gymloadapi.modulos.tipovariacao.service.TipoVariacaoService;
import br.com.gymloadapi.modulos.usuario.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
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
import static br.com.gymloadapi.modulos.tipovariacao.helper.TipoVariacaoHelper.umTipoVariacaoRequest;
import static br.com.gymloadapi.modulos.tipovariacao.helper.TipoVariacaoHelper.umTipoVariacaoRequestComCamposInvalidos;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(TipoVariacaoController.class)
@MockitoBean(types = {UsuarioService.class, BackBlazeService.class})
@Import({SecurityConfiguration.class, TokenService.class, JwtAccessDeinedHandler.class, TestSecurityConfiguration.class})
class TipoVariacaoControllerTest {

    private static final String URL = "/api/tipos-variacoes";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TipoVariacaoService service;

    @Test
    @WithAnonymousUser
    void salvar_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        isUnauthorized(post(URL), mockMvc);
        verifyNoInteractions(service);
    }

    @Test
    @WithMockUser
    void salvar_deveRetornarForbidden_quandoUsuarioNaoForAdmin() {
        isForbidden(post(URL), mockMvc);
        verifyNoInteractions(service);
    }

    @Test
    @WithUserDetails
    void salvar_deveRetornarBadRequest_quandoCamposObrigatoriosInvalidos() {
        isBadRequest(post(URL), mockMvc, umTipoVariacaoRequestComCamposInvalidos(),
            "O campo nome é obrigatório.");

        verifyNoInteractions(service);
    }

    @Test
    @WithUserDetails
    void salvar_deveRetornarCreated_quandoCamposObrigatoriosValidos() {
        var request = umTipoVariacaoRequest();

        isCreated(post(URL), mockMvc, request);

        verify(service).salvar(request, umUsuarioAdmin());
    }

    @EmptySource
    @ParameterizedTest
    @WithAnonymousUser
    @ValueSource(strings = "/select")
    void gets_devemRetornarUnauthorized_quandoUsuarioNaoAutenticado(String endpoint) {
        isUnauthorized(get(URL + endpoint), mockMvc);
        verifyNoInteractions(service);
    }

    @Test
    @WithMockUser
    void buscarTodos_deveRetornarForbidden_quandoUsuarioNaoForAdmin() {
        isForbidden(get(URL), mockMvc);
        verifyNoInteractions(service);
    }

    @Test
    @WithUserDetails
    void buscarTodos_deveRetornarOk_quandoUsuarioForAdmin() {
        isOk(get(URL), mockMvc);

        verify(service).buscarTodos();
    }

    @Test
    @WithAnonymousUser
    void editar_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        isUnauthorized(put(URL + "/1/editar"), mockMvc);
        verifyNoInteractions(service);
    }

    @Test
    @WithMockUser
    void editar_deveRetornarForbidden_quandoUsuarioNaoForAdmin() {
        isForbidden(put(URL + "/1/editar"), mockMvc);
        verifyNoInteractions(service);
    }

    @Test
    @WithUserDetails
    void editar_deveRetornarBadRequest_quandoCamposObrigatoriosInvalidos() {
        isBadRequest(put(URL + "/1/editar"), mockMvc, umTipoVariacaoRequestComCamposInvalidos(),
            "O campo nome é obrigatório.");
        verifyNoInteractions(service);
    }

    @Test
    @WithUserDetails
    void editar_deveRetornarNoContent_quandoCamposObrigatoriosValidos() {
        var request = umTipoVariacaoRequest();

        isNoContent(put(URL + "/1/editar"), mockMvc, request);

        verify(service).editar(1, request, umUsuarioAdmin());
    }

    @Test
    @WithMockUser
    void getSelect_deveRetornarOk_quandoUsuarioAutenticado() {
        isOk(get(URL + "/select"), mockMvc);

        verify(service).getSelect();
    }
}
