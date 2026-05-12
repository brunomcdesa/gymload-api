package br.com.gymloadapi.modulos.exercicio.controller;

import br.com.gymloadapi.autenticacao.service.TokenService;
import br.com.gymloadapi.config.TestSecurityConfiguration;
import br.com.gymloadapi.config.security.JwtAccessDeinedHandler;
import br.com.gymloadapi.config.security.SecurityConfiguration;
import br.com.gymloadapi.modulos.comum.service.BackBlazeService;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioVariacaoService;
import br.com.gymloadapi.modulos.usuario.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static br.com.gymloadapi.helper.TestsHelper.*;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.*;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(ExercicioVariacaoController.class)
@MockitoBean(types = {UsuarioService.class, BackBlazeService.class})
@Import({SecurityConfiguration.class, TokenService.class, JwtAccessDeinedHandler.class, TestSecurityConfiguration.class})
class ExercicioVariacaoControllerTest {

    private static final String URL = "/api/exercicios-variacoes";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ExercicioVariacaoService service;

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
    void salvar_deveRetornarBadRequest_quandoUsuarioAdminECamposObrigatoriosInvalidos() {
        var request = umExercicioVariacaoRequestComCamposInvalidos();
        isBadRequest(post(URL), mockMvc, request,
            "O campo exercicioBaseId é obrigatório.");

        verifyNoInteractions(service);
    }

    @Test
    @WithUserDetails
    void salvar_deveRetornarCreated_quandoUsuarioAdminECamposObrigatoriosValidos() {
        var request = umExercicioVariacaoRequestComTipoVariacao();
        isCreated(post(URL), mockMvc, request);

        verify(service).salvar(request, 1);
    }

    @Test
    @WithAnonymousUser
    void buscarVariacoesDoExercicio_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        isUnauthorized(get(URL + "/1"), mockMvc);
        verifyNoInteractions(service);
    }

    @Test
    @WithUserDetails
    void buscarVariacoesDoExercicio_deveRetornarOk_quandoUsuarioAutenticado() {
        isOk(get(URL + "/1"), mockMvc);
        verify(service).buscarVariacoesDoExercicio(1, 1);
    }

    @Test
    @WithAnonymousUser
    void editarVariacao_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        isUnauthorized(put(URL + "/1/editar"), mockMvc);
        verifyNoInteractions(service);
    }

    @Test
    @WithMockUser
    void editarVariacao_deveRetornarForbidden_quandoUsuarioNaoForAdmin() {
        isForbidden(put(URL + "/1/editar"), mockMvc);
        verifyNoInteractions(service);
    }

    @Test
    @WithUserDetails
    void editarVariacao_deveRetornarBadRequest_quandoCamposObrigatoriosInvalidos() {
        var request = umExercicioVariacaoRequest(1, null, null);

        isBadRequest(put(URL + "/1/editar"), mockMvc, request,
            "O campo exercicioBaseId deve ser nulo");

        verifyNoInteractions(service);
    }

    @Test
    @WithUserDetails
    void editarVariacao_deveRetornarNoContent_quandoCamposObrigatoriosValidos() {
        var request = umExercicioVariacaoRequest(null, null, null);

        isNoContent(put(URL + "/1/editar"), mockMvc, request);

        verify(service).editarVariacao(1, request, umUsuarioAdmin());
    }
}
