package br.com.gymloadapi.modulos.comum.types;

import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import lombok.Value;

import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Value
public class Email {

    String valor;

    public Email(String email) {
        this.validar(email);
        this.valor = email;
    }

    private void validar(String email) {
        if (isBlank(email)) {
            throw new ValidacaoException("O E-mail é obrigatório.");
        }

        var emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        var pattern = Pattern.compile(emailRegex);

        if (!pattern.matcher(email).matches()) {
            throw new ValidacaoException("Formato de e-mail inválido.");
        }
    }
}
