package br.com.gymloadapi.modulos.comum.helper;

import br.com.gymloadapi.modulos.comum.types.Email;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ComumHelper {

    public static Email umEmail() {
        return new Email("teste@teste.com");
    }

    public static Email umEmailAdmin() {
        return new Email("testeAdmin@teste.com");
    }
}
