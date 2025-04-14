package br.com.gymloadapi.modulos.registroatividade.controller;

import br.com.gymloadapi.autenticacao.service.TokenService;
import br.com.gymloadapi.config.TestSecurityConfiguration;
import br.com.gymloadapi.config.security.JwtAccessDeinedHandler;
import br.com.gymloadapi.config.security.SecurityConfiguration;
import br.com.gymloadapi.modulos.registroatividade.service.RegistroAtividadeService;
import br.com.gymloadapi.modulos.usuario.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static br.com.gymloadapi.helper.TestsHelper.*;
import static br.com.gymloadapi.modulos.registroatividade.helper.RegistroAtividadeHelper.umRegistroAtividadeRequestComCamposNull;
import static br.com.gymloadapi.modulos.registroatividade.helper.RegistroAtividadeHelper.umRegistroAtividadeRequestParaMusculacao;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@MockitoBean(types = UsuarioService.class)
@WebMvcTest(RegistroAtividadeController.class)
@Import({SecurityConfiguration.class, TokenService.class, JwtAccessDeinedHandler.class, TestSecurityConfiguration.class})
class RegistroAtividadeControllerTest {

    private static final String URL = "/api/registro-atividades";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private RegistroAtividadeService service;

    @Test
    @WithAnonymousUser
    void salvar_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        isUnauthorized(post(URL), mockMvc);
        verifyNoInteractions(service);
    }

    @Test
    @WithMockUser
    void salvar_deveRetornarBadRequest_quandoCamposObrigatoriosInvalidos() {
        isBadRequest(post(URL), mockMvc, umRegistroAtividadeRequestComCamposNull(), "O campo exercicioId é obrigatório.");

        verifyNoInteractions(service);
    }

    @Test
    @WithUserDetails
    void salvar_deveRetornarCreated_quandoCamposObrigatoriosValidos() {
        var request = umRegistroAtividadeRequestParaMusculacao();
        isCreated(post(URL), mockMvc, request);

        verify(service).salvar(request, umUsuarioAdmin());
    }

    @ParameterizedTest
    @WithAnonymousUser
    @ValueSource(strings = {"/1", "/1/completo"})
    void gets_devemRetornarUnauthorized_quandoUsuarioNaoAutenticado(String endpoint) {
        isUnauthorized(get(URL + endpoint), mockMvc);
        verifyNoInteractions(service);
    }

    @WithUserDetails
    @ParameterizedTest
    @ValueSource(strings = {"/1", "/1/completo"})
    void gets_devemRetornarOk_quandoUsuarioAutenticado(String endpoint) {
        isOk(get(URL + endpoint), mockMvc);

        Map.<String, Runnable>of(
            "/1", () -> verify(service).buscarUltimoRegistroAtividade(1, 1),
            "/1/completo", () -> verify(service).buscarRegistroAtividadeCompleto(1, 1)
        ).get(endpoint).run();
    }

    @Test
    @WithAnonymousUser
    void editar_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        isUnauthorized(put(URL + "/1/editar"), mockMvc);
        verifyNoInteractions(service);
    }

    @Test
    @WithMockUser
    void editar_deveRetornarBadRequest_quandoCamposObrigatoriosInvalidos() {
        isBadRequest(put(URL + "/1/editar"), mockMvc, umRegistroAtividadeRequestComCamposNull(),
            "O campo exercicioId é obrigatório.");

        verifyNoInteractions(service);
    }

    @Test
    @WithUserDetails
    void editar_deveRetornarNoContent_quandoCamposObrigatoriosValidos() {
        var request = umRegistroAtividadeRequestParaMusculacao();
        isNoContent(put(URL + "/1/editar"), mockMvc, request);

        verify(service).editar(1, request, umUsuarioAdmin());
    }
}
