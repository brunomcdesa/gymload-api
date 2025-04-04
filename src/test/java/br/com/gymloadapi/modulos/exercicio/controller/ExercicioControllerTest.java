package br.com.gymloadapi.modulos.exercicio.controller;

import br.com.gymloadapi.autenticacao.service.TokenService;
import br.com.gymloadapi.config.security.JwtAccessDeinedHandler;
import br.com.gymloadapi.config.security.SecurityConfiguration;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioRequest;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static br.com.gymloadapi.helper.TestsHelper.*;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioRequest;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(ExercicioController.class)
@MockitoBean(types = UsuarioService.class)
@Import({SecurityConfiguration.class, TokenService.class, JwtAccessDeinedHandler.class})
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
        var request = new ExercicioRequest(exercicioNome, null, null, null, null);
        isBadRequest(post(URL), mockMvc, request,
            "O campo nome é obrigatório.",
            "O campo grupoMuscularId é obrigatório.",
            "O campo tipoExercicio é obrigatório.",
            "O campo tipoPegada é obrigatório.");

        verifyNoInteractions(service);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void salvar_deveRetornarCreated_quandoUsuarioAdminECamposObrigatoriosValidos() {
        var request = umExercicioRequest();
        isCreated(post(URL), mockMvc, request);

        verify(service).salvar(request);
    }

    @EmptySource
    @ParameterizedTest
    @WithAnonymousUser
    @ValueSource(strings = {"/select", "/treino/1"})
    void gets_devemRetornarUnauthorized_quandoUsuarioNaoAutenticado(String endpoint) {
        isUnauthorized(post(URL + endpoint), mockMvc);
        verifyNoInteractions(service);
    }

    @EmptySource
    @WithMockUser
    @ParameterizedTest
    @ValueSource(strings = {"/select", "/treino/1"})
    void gets_devemRetornarOk_quandoUsuarioNaoAutenticado(String endpoint) {
        isOk(get(URL + endpoint), mockMvc);

        Map.<String, Runnable>of(
            "", () -> verify(service).buscarTodos(),
            "/select", () -> verify(service).buscarTodosSelect(),
            "/treino/1", () -> verify(service).buscarExerciciosPorTreino(1)
        ).get(endpoint).run();
    }

}
