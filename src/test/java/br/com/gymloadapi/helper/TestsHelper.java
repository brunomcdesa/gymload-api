package br.com.gymloadapi.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@UtilityClass
@SuppressWarnings("ParameterNumber")
public class TestsHelper {

    private static ObjectMapper getMapper() {
        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    public static MockMultipartFile umMockMultipartFile() {
        return new MockMultipartFile("imagem", "imagem.jpeg",
            APPLICATION_OCTET_STREAM_VALUE, "imagem".getBytes());
    }

    @SneakyThrows
    public static byte[] convertObjectToJsonBytes(Object object) {
        return getMapper().writeValueAsBytes(object);
    }

    public static MockMultipartFile converterObjectParaMultipart(String nome, Object json) {
        return new MockMultipartFile(nome, null, APPLICATION_JSON_VALUE, convertObjectToJsonBytes(json));
    }

    @SneakyThrows
    public static void isUnauthorized(MockHttpServletRequestBuilder endpoint, MockMvc mockMvc) {
        mockMvc.perform(endpoint
                .contentType(APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @SneakyThrows
    public static void isForbidden(MockHttpServletRequestBuilder endpoint, MockMvc mvc) {
        mvc.perform(endpoint
                .contentType(APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @SneakyThrows
    public static void isBadRequest(MockHttpServletRequestBuilder endpoint, MockMvc mvc,
                                    Object request, String... erros) {
        mvc.perform(endpoint
                .contentType(APPLICATION_JSON)
                .content(convertObjectToJsonBytes(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message",
                containsInAnyOrder(erros)));
    }

    @SneakyThrows
    public static void isBadRequest(MockHttpServletRequestBuilder endpoint, MockMvc mvc,
                                    String... erros) {
        mvc.perform(endpoint
                .contentType(APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message",
                containsInAnyOrder(erros)));
    }

    @SneakyThrows
    public static void isBadRequestMultipart(HttpMethod method, String endpoint, MockMvc mvc, MockMultipartFile file,
                                             String requestName, Object request, String... erros) {
        performMultipart(mvc, method, endpoint, file, requestName, request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(erros)));
    }

    @SneakyThrows
    public static void isOk(MockHttpServletRequestBuilder endpoint, MockMvc mockMvc) {
        mockMvc.perform(endpoint
                .contentType(APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @SneakyThrows
    public static void isOk(MockHttpServletRequestBuilder endpoint, MockMvc mockMvc, Object request) {
        mockMvc.perform(endpoint
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(convertObjectToJsonBytes(request)))
            .andExpect(status().isOk());
    }

    @SneakyThrows
    public static void isCreated(MockHttpServletRequestBuilder endpoint, MockMvc mockMvc, Object request) {
        mockMvc.perform(endpoint
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(convertObjectToJsonBytes(request)))
            .andExpect(status().isCreated());
    }

    @SneakyThrows
    public static void isCreatedMultipart(HttpMethod method, String endpoint, MockMvc mvc, MockMultipartFile file,
                                          String requestName, Object request) {
        performMultipart(mvc, method, endpoint, file, requestName, request)
            .andExpect(status().isCreated());
    }

    @SneakyThrows
    public static void isNoContent(MockHttpServletRequestBuilder endpoint, MockMvc mvc, Object request) {
        mvc.perform(endpoint
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(convertObjectToJsonBytes(request)))
            .andExpect(status().isNoContent());
    }

    @SneakyThrows
    public static void isNoContent(MockHttpServletRequestBuilder endpoint, MockMvc mvc) {
        mvc.perform(endpoint
                .accept(APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @SneakyThrows
    public static void isNoContentMultipart(HttpMethod method, String endpoint, MockMvc mvc, MockMultipartFile file,
                                            String requestName, Object request) {
        performMultipart(mvc, method, endpoint, file, requestName, request)
            .andExpect(status().isNoContent());
    }

    @SneakyThrows
    private static ResultActions performMultipart(MockMvc mvc, HttpMethod method, String endpoint,
                                                  MockMultipartFile file, String requestName,
                                                  Object request) {
        return mvc.perform(multipart(method, endpoint)
            .file(file)
            .file(converterObjectParaMultipart(requestName, request))
            .accept(ALL_VALUE));
    }
}
