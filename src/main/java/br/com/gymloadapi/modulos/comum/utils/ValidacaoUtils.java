package br.com.gymloadapi.modulos.comum.utils;

import br.com.gymloadapi.modulos.comum.exception.PermissaoException;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.experimental.UtilityClass;

import java.util.Objects;

import static java.lang.String.format;

@UtilityClass
public class ValidacaoUtils {

    public static void validarUsuarioAlteracao(Integer usuarioDoObjetoId, Usuario usuarioAutenticado, String message) {
        if (!usuarioAutenticado.isAdmin() && !Objects.equals(usuarioAutenticado.getId(), usuarioDoObjetoId)) {
            throw new PermissaoException(format("Apenas usuários Admin ou o próprio usuário podem %s.", message));
        }
    }
}
