package br.com.gymloadapi.modulos.gradesemanal.controller;

import br.com.gymloadapi.autenticacao.service.TokenService;
import br.com.gymloadapi.config.TestSecurityConfiguration;
import br.com.gymloadapi.config.security.JwtAccessDeinedHandler;
import br.com.gymloadapi.config.security.SecurityConfiguration;
import br.com.gymloadapi.modulos.comum.service.BackBlazeService;
import br.com.gymloadapi.modulos.gradesemanal.dto.GradeSemanalHojeResponse;
import br.com.gymloadapi.modulos.gradesemanal.dto.GradeSemanalRequest;
import br.com.gymloadapi.modulos.gradesemanal.service.GradeSemanalService;
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

import java.util.List;
import java.util.Optional;

import static br.com.gymloadapi.helper.TestsHelper.isBadRequest;
import static br.com.gymloadapi.helper.TestsHelper.isNoContent;
import static br.com.gymloadapi.helper.TestsHelper.isOk;
import static br.com.gymloadapi.helper.TestsHelper.isUnauthorized;
import static br.com.gymloadapi.modulos.comum.enums.EDiaSemana.SEGUNDA;
import static br.com.gymloadapi.modulos.gradesemanal.helper.GradeSemanalHelper.umGradeSemanalRequest;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GradeSemanalController.class)
@MockitoBean(types = {UsuarioService.class, BackBlazeService.class})
@Import({SecurityConfiguration.class, TokenService.class, JwtAccessDeinedHandler.class, TestSecurityConfiguration.class})
class GradeSemanalControllerTest {

    private static final String URL = "/api/grade-semanal";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private GradeSemanalService service;

    @Test
    @WithAnonymousUser
    void buscarHoje_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        isUnauthorized(get(URL + "/hoje"), mockMvc);
        verifyNoInteractions(service);
    }

    @Test
    @WithUserDetails
    void buscarHoje_deveRetornarOk_quandoExistirTreinoParaHoje() {
        when(service.buscarHoje(1))
            .thenReturn(Optional.of(new GradeSemanalHojeResponse(1, "Um Treino", false)));
        isOk(get(URL + "/hoje"), mockMvc);
        verify(service).buscarHoje(1);
    }

    @Test
    @WithUserDetails
    void buscarHoje_deveRetornarNoContent_quandoNaoExistirTreinoParaHoje() throws Exception {
        when(service.buscarHoje(1)).thenReturn(Optional.empty());
        mockMvc.perform(get(URL + "/hoje"))
            .andExpect(status().isNoContent());
        verify(service).buscarHoje(1);
    }

    @Test
    @WithAnonymousUser
    void listar_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        isUnauthorized(get(URL), mockMvc);
        verifyNoInteractions(service);
    }

    @Test
    @WithUserDetails
    void listar_deveRetornarOk_quandoUsuarioAutenticado() {
        when(service.listar(1)).thenReturn(List.of());
        isOk(get(URL), mockMvc);
        verify(service).listar(1);
    }

    @Test
    @WithAnonymousUser
    void salvar_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        isUnauthorized(put(URL + "/SEGUNDA"), mockMvc);
        verifyNoInteractions(service);
    }

    @Test
    @WithMockUser
    void salvar_deveRetornarBadRequest_quandoTreinoIdNulo() {
        var request = new GradeSemanalRequest(null);
        isBadRequest(put(URL + "/SEGUNDA"), mockMvc, request, "O campo treinoId é obrigatório.");
        verifyNoInteractions(service);
    }

    @Test
    @WithUserDetails
    void salvar_deveRetornarNoContent_quandoCamposObrigatoriosValidos() {
        var request = umGradeSemanalRequest();
        isNoContent(put(URL + "/SEGUNDA"), mockMvc, request);
        verify(service).salvar(SEGUNDA, request,
            br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin());
    }

    @Test
    @WithAnonymousUser
    void remover_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        isUnauthorized(delete(URL + "/SEGUNDA"), mockMvc);
        verifyNoInteractions(service);
    }

    @Test
    @WithUserDetails
    void remover_deveRetornarNoContent_quandoUsuarioAutenticado() {
        isNoContent(delete(URL + "/SEGUNDA"), mockMvc);
        verify(service).remover(SEGUNDA, 1);
    }
}
