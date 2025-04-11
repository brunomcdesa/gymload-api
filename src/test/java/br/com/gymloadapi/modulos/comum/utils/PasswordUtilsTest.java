package br.com.gymloadapi.modulos.comum.utils;

import org.junit.jupiter.api.Test;

import static br.com.gymloadapi.modulos.comum.utils.PasswordUtils.encodePassword;
import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilsTest {

    @Test
    void encodePassword_deveRetornarSenhaEncriptada_quandoSenhaValida() {
        assertTrue(encodePassword("minhasenha123").startsWith("$2a$"));
    }

    @Test
    void encodePassword_deveRetornarHashesDiferentes_quandoMesmaSenhaEncriptadaDuasVezes() {
        var senha = "minhasenha123";
        assertNotEquals(encodePassword(senha), encodePassword(senha));
    }

    @Test
    void isPasswordEquals_deveRetornarTrue_quandoSenhaCorresponderAoHash() {
        var senha = "minhasenha123";
        assertTrue(PasswordUtils.isPasswordEquals(senha, encodePassword(senha)));
    }

    @Test
    void isPasswordEquals_deveRetornarFalse_quandoSenhaNaoCorresponderAoHash() {
        assertFalse(PasswordUtils.isPasswordEquals("abc12389", encodePassword("minhasenha123")));
    }
}
