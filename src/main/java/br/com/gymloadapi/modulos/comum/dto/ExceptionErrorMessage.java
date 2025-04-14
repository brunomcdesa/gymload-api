package br.com.gymloadapi.modulos.comum.dto;

import static java.lang.String.format;

public record ExceptionErrorMessage(
    String message,
    String field
) {

    public static ExceptionErrorMessage createErrorMessage(String message, String field) {
        return new ExceptionErrorMessage(format("O campo %s %s", field, message), field);
    }
}
