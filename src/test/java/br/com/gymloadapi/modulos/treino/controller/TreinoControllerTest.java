package br.com.gymloadapi.modulos.treino.controller;

import br.com.gymloadapi.autenticacao.service.TokenService;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static br.com.gymloadapi.helper.TestsHelper.*;
import static br.com.gymloadapi.modulos.treino.helper.TreinoHelper.umTreinoRequest;
import static java.util.Collections.emptyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(TreinoController.class)
@MockitoBean(types = UsuarioService.class)
@Import({SecurityConfiguration.class, TokenService.class, JwtAccessDeinedHandler.class})
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
    @WithMockUser
    void salvar_deveRetornarCreated_quandoCamposObrigatoriosValidos() {
        var request = umTreinoRequest();
        isCreated(post(URL), mockMvc, request);

        verify(service).salvar(request);
    }

    @Test
    @WithAnonymousUser
    void listarTodosDoUsuario_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        isUnauthorized(get(URL), mockMvc);
        verifyNoInteractions(service);
    }

    @Test
    @WithMockUser
    void listarTodosDoUsuario_deveRetornarOk_quandoUsuarioAutenticado() {
        isOk(get(URL), mockMvc);
        verify(service).listarTodosDoUsuario();
    }
}
