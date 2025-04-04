package br.com.gymloadapi.modulos.comum.controller;

import br.com.gymloadapi.modulos.comum.dto.ExceptionErrorMessage;
import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}
