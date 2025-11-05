package br.com.gymloadapi.modulos.comum.utils;

import br.com.gymloadapi.modulos.usuario.model.Usuario;
import org.junit.jupiter.api.Test;

import static br.com.gymloadapi.modulos.comum.utils.MapUtils.*;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuario;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MapUtilsTest {

    @Test
    void mapNull_deveRetornarValorMapeadoDoSupplier_quandoReceverValorValido() {
        assertEquals("teste", mapNull("", () -> "teste"));
    }

    @Test
    void mapNull_deveRetornarValorMapeadoDaFuncion_quandoReceverValorValido() {
        assertEquals("teste", mapNull("teste", string -> string));
    }

    @Test
    void mapNull_deveRetornarNull_quandoReceberValorInvalido() {
        assertNull(mapNull(null, () -> "teste"));
        assertNull(mapNull(null, string -> string));
    }

    @Test
    void mapNullBoolean_deveAplicarAFuncao_quandoValueTrue() {
        assertEquals(2, mapNullBoolean(true, () -> umUsuario().getId()));
    }

    @Test
    void mapNullBoolean_deveRetornarNull_quandoValueFalse() {
        assertNull(mapNullBoolean(false, () -> umUsuario().getId()));
    }

    @Test
    void mapNullWithBackup_deveAplicarAFuncao_quandoValueNaoForNull() {
        assertEquals(2, mapNullWithBackup(umUsuario(), Usuario::getId, 20));
    }

    @Test
    void mapNullWithBackup_deveRetornarBackup_quandoValueForNull() {
        assertEquals(20, mapNullWithBackup(null, Usuario::getId, 20));
    }

}
