package br.com.gymloadapi.autenticacao.helper;

import br.com.gymloadapi.autenticacao.dto.LoginRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AutenticacaoHelper {

    public static LoginRequest umLoginRequest() {
        return new LoginRequest("admin", "654321");
    }
}
