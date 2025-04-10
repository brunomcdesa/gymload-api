package br.com.gymloadapi.modulos.usuario.mapper;

import org.junit.jupiter.api.Test;

import java.util.List;

import static br.com.gymloadapi.modulos.usuario.enums.EUserRole.USER;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioRequest;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UsuarioMapperTest {

    private final UsuarioMapper mapper = new UsuarioMapperImpl();

    @Test
    void mapToModel_deveFazerMapeamentoCorreto_quandoSolicitado() {
        var usuario = mapper.mapToModel(umUsuarioRequest(), "123Abc!@#", List.of(USER));
        assertAll(
            () -> assertEquals("Usuario", usuario.getNome()),
            () -> assertEquals("usuario", usuario.getUsername()),
            () -> assertEquals("123Abc!@#", usuario.getSenha()),
            () -> assertEquals(List.of(USER), usuario.getRoles())
        );
    }

    @Test
    void mapModelToResponse_deveFazerMapeamentoCorreto_quandoSolicitar() {
        var response = mapper.mapModelToResponse(umUsuarioAdmin());
        assertAll(
            () -> assertEquals("c2d83d78-e1b2-4f7f-b79d-1b83f3c435f9", response.uuid().toString()),
            () -> assertEquals("Usuario Admin", response.nome()),
            () -> assertEquals("usuarioAdmin", response.username())
        );
    }
}
