package br.com.gymloadapi.modulos.grupomuscular.controller;

import br.com.gymloadapi.autenticacao.service.TokenService;
import br.com.gymloadapi.config.security.JwtAccessDeinedHandler;
import br.com.gymloadapi.config.security.SecurityConfiguration;
import br.com.gymloadapi.modulos.comum.service.BackBlazeService;
import br.com.gymloadapi.modulos.grupomuscular.dto.GrupoMuscularRequest;
import br.com.gymloadapi.modulos.grupomuscular.service.GrupoMuscularService;
import br.com.gymloadapi.modulos.usuario.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static br.com.gymloadapi.helper.TestsHelper.*;
import static br.com.gymloadapi.modulos.grupomuscular.helper.GrupoMuscularHelper.umGrupoMuscularRequest;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(GrupoMuscularController.class)
@MockitoBean(types = {UsuarioService.class, BackBlazeService.class})
@Import({SecurityConfiguration.class, TokenService.class, JwtAccessDeinedHandler.class})
class GrupoMuscularControllerTest {

    private static final String URL = "/api/grupos-musculares";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private GrupoMuscularService service;

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

    @ParameterizedTest
    @WithMockUser(roles = "ADMIN")
    @CsvSource(value = {"'',''", "'   ','   '", "NULL,NULL"}, nullValues = {"NULL"})
    void salvar_deveRetornarBadRequest_quandoUsuarioForAdminMasCamposObrigatoriosInvalidos(String nome, String codigo) {
        var request = new GrupoMuscularRequest(nome, codigo);
        isBadRequest(post(URL), mockMvc, request,
            "O campo nome é obrigatório.",
            "O campo codigo é obrigatório.");

        verifyNoInteractions(service);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void salvar_deveRetornarCreated_quandoUsuarioForAdminECamposObrigatoriosValidos() {
        var request = umGrupoMuscularRequest();
        isCreated(post(URL), mockMvc, request);

        verify(service).salvar(request);
    }

    @EmptySource
    @ParameterizedTest
    @WithAnonymousUser
    @ValueSource(strings = {"/select"})
    void gets_devemRetornarUnauthorized_quandoUsuarioNaoAutenticado(String endpoint) {
        isUnauthorized(get(URL + endpoint), mockMvc);
        verifyNoInteractions(service);
    }

    @EmptySource
    @WithMockUser
    @ParameterizedTest
    @ValueSource(strings = {"/select"})
    void gets_devemRetornarOk_quandoUsuarioAutenticado(String endpoint) {
        isOk(get(URL + endpoint), mockMvc);

        Map.<String, Runnable>of(
            "", () -> verify(service).findAllResponse(),
            "/select", () -> verify(service).findAllSelect()
        ).get(endpoint).run();
    }
}
