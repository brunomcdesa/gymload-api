package br.com.gymloadapi.modulos.exercicio.controller;

import br.com.gymloadapi.autenticacao.service.TokenService;
import br.com.gymloadapi.config.TestSecurityConfiguration;
import br.com.gymloadapi.config.security.JwtAccessDeinedHandler;
import br.com.gymloadapi.config.security.SecurityConfiguration;
import br.com.gymloadapi.modulos.comum.service.BackBlazeService;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.usuario.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
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

import java.util.Map;

import static br.com.gymloadapi.helper.TestsHelper.*;
import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.CALISTENIA;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(ExercicioController.class)
@MockitoBean(types = {UsuarioService.class, BackBlazeService.class})
@Import({SecurityConfiguration.class, TokenService.class, JwtAccessDeinedHandler.class, TestSecurityConfiguration.class})
class ExercicioControllerTest {

    private static final String URL = "/api/exercicios";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ExercicioService service;

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
    @NullAndEmptySource
    @WithMockUser(roles = "ADMIN")
    @ValueSource(strings = {"    "})
    void salvar_deveRetornarBadRequest_quandoUsuarioAdminECamposObrigatoriosInvalidos(String exercicioNome) {
        var request = umExercicioRequestComCamposInvalidos(exercicioNome);
        isBadRequest(post(URL), mockMvc, request,
            "O campo nome é obrigatório.",
            "O campo tipoExercicio é obrigatório.");

        verifyNoInteractions(service);
    }

    @Test
    @WithUserDetails
    void salvar_deveRetornarCreated_quandoUsuarioAdminECamposObrigatoriosValidos() {
        var request = umExercicioMusculacaoRequest();
        isCreated(post(URL), mockMvc, request);

        verify(service).salvar(request, 1);
    }

    @EmptySource
    @ParameterizedTest
    @WithAnonymousUser
    @ValueSource(strings = {"/select", "/treino/1"})
    void gets_devemRetornarUnauthorized_quandoUsuarioNaoAutenticado(String endpoint) {
        isUnauthorized(post(URL + endpoint), mockMvc);
        verifyNoInteractions(service);
    }

    @WithMockUser
    @ParameterizedTest
    @ValueSource(strings = {"/select", "/treino/1"})
    void gets_devemRetornarOk_quandoUsuarioNaoAutenticado(String endpoint) {
        isOk(get(URL + endpoint), mockMvc);

        Map.<String, Runnable>of(
            "/select", () -> verify(service).buscarTodosSelect(),
            "/treino/1", () -> verify(service).buscarExerciciosPorTreino(1)
        ).get(endpoint).run();
    }

    @Test
    @WithMockUser
    void buscarTodos_deveRetornarBadRequest_quandoFiltrosObrigatoriosInvalidos() {
        isBadRequest(get(URL), mockMvc, "O campo tipoExercicio é obrigatório.");

        verifyNoInteractions(service);
    }

    @Test
    @WithMockUser
    void buscarTodos_deveRetornarOk_quandoFiltrosObrigatoriosValidos() {
        isOk(get(URL)
                .param("tipoExercicio", CALISTENIA.name())
                .param("grupoMuscularId", "2"),
            mockMvc);

        verify(service).buscarTodos(umExercicioFiltro());
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

    @ParameterizedTest
    @NullAndEmptySource
    @WithMockUser(roles = "ADMIN")
    @ValueSource(strings = {"    "})
    void editar_deveRetornarBadRequest_quandoUsuarioAdminECamposObrigatoriosInvalidos(String exercicioNome) {
        var request = umExercicioRequestComCamposInvalidos(exercicioNome);
        isBadRequest(put(URL + "/1/editar"), mockMvc, request,
            "O campo nome é obrigatório.",
            "O campo tipoExercicio é obrigatório.");

        verifyNoInteractions(service);
    }

    @Test
    @WithUserDetails
    void editar_deveRetornarNoContent_quandoUsuarioAdminECamposObrigatoriosValidos() {
        var request = umExercicioMusculacaoRequest();
        isNoContent(put(URL + "/1/editar"), mockMvc, request);

        verify(service).editar(1, request, 1);
    }
}
