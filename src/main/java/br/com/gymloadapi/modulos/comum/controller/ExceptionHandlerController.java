package br.com.gymloadapi.modulos.comum.controller;

import br.com.gymloadapi.modulos.comum.dto.ExceptionErrorMessage;
import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.PermissaoException;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import java.util.List;

import static br.com.gymloadapi.modulos.comum.dto.ExceptionErrorMessage.createErrorMessage;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ExceptionErrorMessage handleNotFoundException(NotFoundException exception) {
        return new ExceptionErrorMessage(exception.getMessage(), null);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(ValidacaoException.class)
    public ExceptionErrorMessage handleValidacaoException(ValidacaoException exception) {
        return new ExceptionErrorMessage(exception.getMessage(), null);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public List<ExceptionErrorMessage> handleBindException(BindException exception) {
        return exception.getBindingResult().getFieldErrors().stream()
            .map(error -> createErrorMessage(error.getDefaultMessage(), error.getField()))
            .toList();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public List<ExceptionErrorMessage> handleConstraintViolationException(ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream()
            .map(violation -> createErrorMessage(violation.getMessage(), violation.getPropertyPath().toString()))
            .toList();
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(PermissaoException.class)
    public ExceptionErrorMessage handlePermissaoException(PermissaoException exception) {
        return new ExceptionErrorMessage(format("Acesso negado. %s", exception.getMessage()), null);
    }
}
