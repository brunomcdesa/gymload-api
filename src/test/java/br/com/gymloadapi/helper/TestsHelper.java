package br.com.gymloadapi.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@UtilityClass
public class TestsHelper {

    private static ObjectMapper getMapper() {
        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @SneakyThrows
    public static byte[] convertObjectToJsonBytes(Object object) {
        return getMapper().writeValueAsBytes(object);
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
}
