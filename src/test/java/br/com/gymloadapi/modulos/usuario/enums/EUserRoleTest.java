package br.com.gymloadapi.modulos.usuario.enums;

import org.junit.jupiter.api.Test;

import static br.com.gymloadapi.modulos.usuario.enums.EUserRole.ADMIN;
import static br.com.gymloadapi.modulos.usuario.enums.EUserRole.USER;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EUserRoleTest {

    @Test
    void mapRole_deveFazerOMapeamentoParaARole_quandoSolicitado() {
        assertAll(
            () -> assertEquals("ROLE_ADMIN", ADMIN.mapRole()),
            () -> assertEquals("ROLE_USER", USER.mapRole())
        );
    }
}
