package br.com.gymloadapi.modulos.usuario.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EUserRole {

    ADMIN("admin"),
    USER("user");

    private final String role;

    public String mapRole() {
        return "ROLE_" + this.name();
    }
}
