package br.com.gymloadapi.autenticacao.helper;

import br.com.gymloadapi.autenticacao.dto.LoginRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AutenticacaoHelper {

    public static LoginRequest umLoginAdminRequest() {
        return new LoginRequest("admin", "654321");
    }

    public static LoginRequest umLoginUserRequest(String password) {
        return new LoginRequest("usuarioUser", password);
    }
}
