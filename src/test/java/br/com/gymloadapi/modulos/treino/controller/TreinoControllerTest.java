package br.com.gymloadapi.modulos.treino.controller;

import br.com.gymloadapi.autenticacao.service.TokenService;
import br.com.gymloadapi.config.TestSecurityConfiguration;
import br.com.gymloadapi.config.security.JwtAccessDeinedHandler;
import br.com.gymloadapi.config.security.SecurityConfiguration;
import br.com.gymloadapi.modulos.treino.dto.TreinoRequest;
import br.com.gymloadapi.modulos.treino.service.TreinoService;
import br.com.gymloadapi.modulos.usuario.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static br.com.gymloadapi.helper.TestsHelper.*;
import static br.com.gymloadapi.modulos.treino.helper.TreinoHelper.umTreinoRequest;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;
import static java.util.Collections.emptyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(TreinoController.class)
@MockitoBean(types = UsuarioService.class)
@Import({SecurityConfiguration.class, TokenService.class, JwtAccessDeinedHandler.class, TestSecurityConfiguration.class})
class TreinoControllerTest {

    private static final String URL = "/api/treinos";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TreinoService service;

    @Test
    @WithAnonymousUser
    void salvar_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        isUnauthorized(post(URL), mockMvc);
        verifyNoInteractions(service);
    }

    @WithMockUser
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"    "})
    void salvar_deveRetornarBadRequest_quandoCamposObrigatoriosInvalidos(String nome) {
        var request = new TreinoRequest(nome, emptyList());
        isBadRequest(post(URL), mockMvc, request,
            "O campo nome é obrigatório.",
            "O campo exerciciosIds é obrigatório.");

        verifyNoInteractions(service);
    }

    @Test
    @WithUserDetails
    void salvar_deveRetornarCreated_quandoCamposObrigatoriosValidos() {
        var request = umTreinoRequest();
        isCreated(post(URL), mockMvc, request);

        verify(service).salvar(request, umUsuarioAdmin());
    }

    @Test
    @WithAnonymousUser
    void listarTodosDoUsuario_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        isUnauthorized(get(URL), mockMvc);
        verifyNoInteractions(service);
    }

    @Test
    @WithUserDetails
    void listarTodosDoUsuario_deveRetornarOk_quandoUsuarioAutenticado() {
        isOk(get(URL), mockMvc);
        verify(service).listarTodosDoUsuario(1);
    }

    @ParameterizedTest
    @WithAnonymousUser
    @ValueSource(strings = {"/1/editar", "/1/ativar", "/1/inativar"})
    void puts_devemRetornarUnauthorized_quandoUsuarioNaoAutenticado(String endpoint) {
        isUnauthorized(put(URL + endpoint), mockMvc);
        verifyNoInteractions(service);
    }

    @WithMockUser
    @ParameterizedTest
    @NullAndEmptySource
    void editar_deveRetornarBadRequest_quandoCamposObrigatoriosInvalidos(List<Integer> exerciciosIds) {
        var request = new TreinoRequest(" ", exerciciosIds);
        isBadRequest(put(URL + "/1/editar"), mockMvc, request,
            "O campo nome é obrigatório.",
            "O campo exerciciosIds é obrigatório.");

        verifyNoInteractions(service);
    }

    @Test
    @WithUserDetails
    void editar_deveRetornarNoContent_quandoCamposObrigatoriosInvalidos() {
        var request = umTreinoRequest();
        isNoContent(put(URL + "/1/editar"), mockMvc, request);

        verify(service).editar(1, request, 1);
    }

    @WithUserDetails
    @ParameterizedTest
    @ValueSource(strings = {"/1/ativar", "/1/inativar"})
    void puts_devemRetornarNoContent_quandoUsuarioAutenticado(String endpoint) {
        isNoContent(put(URL + endpoint), mockMvc);

        Map.<String, Runnable>of(
            "/1/ativar", () -> verify(service).ativar(1, 1),
            "/1/inativar", () -> verify(service).inativar(1, 1)
        ).get(endpoint).run();
    }
}
