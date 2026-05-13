package br.com.gymloadapi.autenticacao.helper;

import br.com.gymloadapi.autenticacao.dto.LoginRequest;
import br.com.gymloadapi.autenticacao.dto.RedefinirSenhaRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AutenticacaoHelper {

    public static LoginRequest umLoginAdminRequest() {
        return new LoginRequest("usuarioAdmin", "654321");
    }

    public static LoginRequest umLoginAdminRequestComEmail() {
        return new LoginRequest("testeAdmin@teste.com", "654321");
    }

    public static LoginRequest umLoginUserRequest(String password) {
        return new LoginRequest("usuarioUser", password);
    }

    public static RedefinirSenhaRequest umRedefinirSenhaRequestPorUsername(String password) {
        return new RedefinirSenhaRequest("usuarioUser", password);
    }

    public static RedefinirSenhaRequest umRedefinirSenhaRequestPorEmail(String password) {
        return new RedefinirSenhaRequest("usuario@email.com", password);
    }
}
