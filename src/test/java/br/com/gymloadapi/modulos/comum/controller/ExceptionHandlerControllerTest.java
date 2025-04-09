package br.com.gymloadapi.modulos.comum.controller;

import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.PermissaoException;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
class ExceptionHandlerControllerTest {

    @InjectMocks
    private ExceptionHandlerController exceptionHandlerController;
    @Mock
    private BindException bindException;
    @Mock
    private BindingResult bindingResult;

    @Test
    @SneakyThrows
    void handleNotFoundException_deveRetornarStatusNotFound_quandoNotFoundExceptionForLancada() {
        var exception = new NotFoundException("Recurso não encontrado");

        var annotation = ExceptionHandlerController.class.getMethod("handleNotFoundException", NotFoundException.class)
            .getAnnotation(ResponseStatus.class);
        var resultado = exceptionHandlerController.handleNotFoundException(exception);

        assertAll(
            () -> assertEquals(NOT_FOUND, annotation.value()),
            () -> assertEquals("Recurso não encontrado", resultado.message()),
            () -> assertNull(resultado.field())
        );
    }

    @Test
    @SneakyThrows
    void handleValidacaoException_deveRetornarStatusBadRequest_quandoValidacaoExceptionForLancado() {
        var exception = new ValidacaoException("Validação falhou");

        var annotation = ExceptionHandlerController.class.getMethod("handleValidacaoException", ValidacaoException.class)
            .getAnnotation(ResponseStatus.class);
        var resultado = exceptionHandlerController.handleValidacaoException(exception);

        assertAll(
            () -> assertEquals(BAD_REQUEST, annotation.value()),
            () -> assertEquals("Validação falhou", resultado.message()),
            () -> assertNull(resultado.field())
        );
    }

    @Test
    @SneakyThrows
    void handleBindException_deveRetornarBadRequest_quandoBindExceptionForLancado() {
        var fieldError1 = new FieldError("objeto", "nome", "é obrigatório");
        var fieldError2 = new FieldError("objeto", "email", "deve ser válido");

        when(bindException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        var annotation = ExceptionHandlerController.class.getMethod("handleBindException", BindException.class)
            .getAnnotation(ResponseStatus.class);
        var resultado = exceptionHandlerController.handleBindException(bindException);

        assertAll(
            () -> assertEquals(BAD_REQUEST, annotation.value()),
            () -> assertEquals(2, resultado.size()),
            () -> assertEquals("O campo nome é obrigatório", resultado.getFirst().message()),
            () -> assertEquals("nome", resultado.getFirst().field()),
            () -> assertEquals("O campo email deve ser válido", resultado.getLast().message()),
            () -> assertEquals("email", resultado.getLast().field())
        );
    }

    @Test
    @SneakyThrows
    void handlePermissaoException_deveRetornarStatusForbidden_quandoPermissaoExceptionForLancado() {
        var exception = new PermissaoException("Sem permissão para a operação");

        var annotation = ExceptionHandlerController.class.getMethod("handlePermissaoException", PermissaoException.class)
            .getAnnotation(ResponseStatus.class);
        var resultado = exceptionHandlerController.handlePermissaoException(exception);

        assertAll(
            () -> assertEquals(FORBIDDEN, annotation.value()),
            () -> assertEquals("Acesso negado. Sem permissão para a operação", resultado.message()),
            () -> assertNull(resultado.field())
        );
    }
}
