package br.com.gymloadapi.modulos.historicocargas.controller;

import br.com.gymloadapi.autenticacao.service.TokenService;
import br.com.gymloadapi.config.TestSecurityConfiguration;
import br.com.gymloadapi.config.security.JwtAccessDeinedHandler;
import br.com.gymloadapi.config.security.SecurityConfiguration;
import br.com.gymloadapi.modulos.historicocargas.service.HistoricoCargasService;
import br.com.gymloadapi.modulos.usuario.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static br.com.gymloadapi.helper.TestsHelper.*;
import static br.com.gymloadapi.modulos.historicocargas.helper.HistoricoCargasHelper.umHistoricoCargasRequest;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@MockitoBean(types = UsuarioService.class)
@WebMvcTest(HistoricoCargasController.class)
@Import({SecurityConfiguration.class, TokenService.class, JwtAccessDeinedHandler.class, TestSecurityConfiguration.class})
class HistoricoCargasControllerTest {

    private static final String URL = "/api/historico-cargas";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private HistoricoCargasService service;

    @Test
    @WithAnonymousUser
    void salvar_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        isUnauthorized(post(URL), mockMvc);
        verifyNoInteractions(service);
    }

    @Test
    @WithUserDetails
    void salvar_deveRetornarCreated_quandoUsuarioAutenticado() {
        var request = umHistoricoCargasRequest();
        isCreated(post(URL), mockMvc, request);

        verify(service).salvar(request, umUsuarioAdmin());
    }

    @WithAnonymousUser
    @ParameterizedTest
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

        var usuarioId = UUID.fromString("c2d83d78-e1b2-4f7f-b79d-1b83f3c435f9");
        Map.<String, Runnable>of(
            "/1", () -> verify(service).buscarUltimoHistoricoCargas(1, usuarioId),
            "/1/completo", () -> verify(service).buscarHistoricoCargasCompleto(1, usuarioId)
        ).get(endpoint).run();
    }
}
