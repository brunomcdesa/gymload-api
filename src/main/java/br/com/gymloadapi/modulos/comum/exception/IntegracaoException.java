package br.com.gymloadapi.modulos.comum.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

public class IntegracaoException extends RuntimeException {

    public IntegracaoException(String message) {
        super(message);
    }

    public IntegracaoException(final Throwable cause, final String className, final String erro) {
        super(erro);

        Logger.getLogger(className)
            .log(Level.SEVERE, String.format("%s [Causa: %s]", erro, cause.toString()), cause);
    }
}
