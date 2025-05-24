package br.com.gymloadapi.modulos.comum.types;

import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "    ")
    void construtor_deveLancarException_quandoValueInvalido(String value) {
        var exception = assertThrowsExactly(ValidacaoException.class, () -> new Email(value));
        assertEquals("O E-mail é obrigatório.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"email", "email.com", "email@email", "email @email.com"})
    void construtor_deveLancarException_quandoValueNaoEstiverNoFormatoValido(String value) {
        var exception = assertThrowsExactly(ValidacaoException.class, () -> new Email(value));
        assertEquals("Formato de e-mail inválido.", exception.getMessage());
    }

    @Test
    void construtor_deveCriarEmail_quandoValueEstiverNoFormatoValido() {
        var email = assertDoesNotThrow(() -> new Email("teste@teste.com")).getValor();
        assertEquals("teste@teste.com", email);
    }
}
