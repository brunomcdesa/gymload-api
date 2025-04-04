package br.com.gymloadapi.modulos.comum.controller;

import br.com.gymloadapi.modulos.comum.dto.ExceptionErrorMessage;
import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

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

    @ResponseBody
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ExceptionErrorMessage> bindValidationError(BindException exception) {
        return exception.getBindingResult().getFieldErrors().stream()
            .map(error -> {
                var field = error.getField();
                return new ExceptionErrorMessage(
                    format("O campo %s %s", field,  error.getDefaultMessage()),
                    field
                );
            }).toList();
    }
}
