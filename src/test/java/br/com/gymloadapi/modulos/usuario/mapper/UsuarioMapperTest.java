package br.com.gymloadapi.modulos.usuario.mapper;

import org.junit.jupiter.api.Test;

import java.util.List;

import static br.com.gymloadapi.modulos.comum.enums.ESexo.MASCULINO;
import static br.com.gymloadapi.modulos.comum.helper.ComumHelper.umEmail;
import static br.com.gymloadapi.modulos.usuario.enums.EUserRole.USER;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioRequest;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UsuarioMapperTest {

    private final UsuarioMapper mapper = new UsuarioMapperImpl();

    @Test
    void mapToModel_deveFazerMapeamentoCorreto_quandoSolicitado() {
        var usuario = mapper.mapToModel(umUsuarioRequest(), "123Abc!@#", List.of(USER), umEmail());
        assertAll(
            () -> assertEquals("Usuario", usuario.getNome()),
            () -> assertEquals("teste@teste.com", usuario.getEmail().getValor()),
            () -> assertEquals("usuario", usuario.getUsername()),
            () -> assertEquals("123Abc!@#", usuario.getSenha()),
            () -> assertEquals(List.of(USER), usuario.getRoles()),
            () -> assertEquals(22, usuario.getIdade()),
            () -> assertEquals(82.5, usuario.getPesoCorporal()),
            () -> assertEquals(1.9, usuario.getAltura()),
            () -> assertEquals(MASCULINO, usuario.getSexo())
        );
    }

    @Test
    void mapModelToResponse_deveFazerMapeamentoCorreto_quandoSolicitar() {
        var response = mapper.mapModelToResponse(umUsuarioAdmin());
        assertAll(
            () -> assertEquals("c2d83d78-e1b2-4f7f-b79d-1b83f3c435f9", response.uuid().toString()),
            () -> assertEquals("Usuario Admin", response.nome()),
            () -> assertEquals("usuarioAdmin", response.username()),
            () -> assertEquals(18, response.idade()),
            () -> assertEquals(50.4, response.pesoCorporal()),
            () -> assertEquals(1.6, response.altura()),
            () -> assertEquals(MASCULINO, response.sexo())
        );
    }

    @Test
    void editar_deveEditarUsuario_quandoSolicitado() {
        var usuario = umUsuarioAdmin();
        mapper.editar(umUsuarioRequest(), usuario);

        assertAll(
            () -> assertEquals("Usuario", usuario.getNome()),
            () -> assertEquals("testeAdmin@teste.com", usuario.getEmail().getValor()),
            () -> assertEquals("usuario", usuario.getUsername()),
            () -> assertEquals(22, usuario.getIdade()),
            () -> assertEquals(82.5, usuario.getPesoCorporal()),
            () -> assertEquals(1.9, usuario.getAltura()),
            () -> assertEquals(MASCULINO, usuario.getSexo())
        );
    }
}
