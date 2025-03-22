package br.com.gymloadapi.modulos.comum.utils;

import br.com.gymloadapi.modulos.usuario.enums.EUserRole;
import lombok.experimental.UtilityClass;

import java.util.List;

import static br.com.gymloadapi.modulos.usuario.enums.EUserRole.ADMIN;
import static br.com.gymloadapi.modulos.usuario.enums.EUserRole.USER;
import static java.util.Arrays.asList;

@UtilityClass
public class RolesUtils {

    public static List<EUserRole> ROLES_ADMIN = asList(ADMIN, USER);
    public static List<EUserRole> ROLES_USER = List.of(USER);
}
