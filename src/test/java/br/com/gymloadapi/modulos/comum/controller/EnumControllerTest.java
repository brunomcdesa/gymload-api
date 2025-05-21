package br.com.gymloadapi.modulos.comum.controller;

import br.com.gymloadapi.autenticacao.service.TokenService;
import br.com.gymloadapi.config.security.JwtAccessDeinedHandler;
import br.com.gymloadapi.config.security.SecurityConfiguration;
import br.com.gymloadapi.modulos.comum.service.BackBlazeService;
import br.com.gymloadapi.modulos.comum.service.EnumService;
import br.com.gymloadapi.modulos.usuario.service.UsuarioService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static br.com.gymloadapi.helper.TestsHelper.isOk;
import static br.com.gymloadapi.helper.TestsHelper.isUnauthorized;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(EnumController.class)
@MockitoBean(types = {UsuarioService.class, BackBlazeService.class})
@Import({SecurityConfiguration.class, TokenService.class, JwtAccessDeinedHandler.class})
class EnumControllerTest {

    private static final String URL = "/api/enums";

    @Autowired
    private MockMvc mvc;
    @MockitoBean
    private EnumService service;

    @WithAnonymousUser
    @ParameterizedTest
    @ValueSource(strings = {"/tipos-exercicios/select", "/tipos-pegadas/select", "/unidades-pesos/select",
        "/tipos-equipamentos/select"})
    void gets_devemRetornarUnauthorized_quandoUsuarioNaoAutenticado(String endpoint) {
        isUnauthorized(get(URL + endpoint), mvc);

        verifyNoInteractions(service);
    }

    @WithMockUser
    @ParameterizedTest
    @ValueSource(strings = {"/tipos-exercicios/select", "/tipos-pegadas/select", "/unidades-pesos/select",
        "/tipos-equipamentos/select"})
    void gets_devemRetornarOk_quandoUsuarioAutenticado(String endpoint) {
        isOk(get(URL + endpoint), mvc);

        Map.<String, Runnable>of(
            "/tipos-exercicios/select", () -> verify(service).getTiposExerciciosSelect(),
            "/tipos-pegadas/select", () -> verify(service).getTiposPegadasSelect(),
            "/unidades-pesos/select", () -> verify(service).getUnidadesPesosSelect(),
            "/tipos-equipamentos/select", () -> verify(service).getTiposEquipamentosSelect()
        ).get(endpoint).run();
    }
}
