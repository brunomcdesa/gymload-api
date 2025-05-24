package br.com.gymloadapi.modulos.comum.converter;

import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static br.com.gymloadapi.modulos.comum.helper.ComumHelper.umEmail;
import static org.junit.jupiter.api.Assertions.*;

class EmailConverterTest {

    private final EmailConverter converter = new EmailConverter();

    @Test
    void convertToDatabaseColumn_deveLancarException_quandoEmailForNull() {
        var exception = assertThrows(ValidacaoException.class, () -> converter.convertToDatabaseColumn(null));
        assertEquals("O Email é obrigatório.", exception.getMessage());
    }

    @Test
    void convertToDatabaseColumn_deveRetornarStringDoEmail_quandoEmailNaoForNull() {
        var email = assertDoesNotThrow(() -> converter.convertToDatabaseColumn(umEmail()));
        assertEquals("teste@teste.com", email);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"    "})
    void convertToEntityAttribute_deveLancarException_quandoDbDataForInvalido(String dbData) {
        var exception = assertThrows(ValidacaoException.class, () -> converter.convertToEntityAttribute(dbData));
        assertEquals("O Email deve ser obrigatório.", exception.getMessage());
    }

    @Test
    void convertToEntityAttribute_deveRetornarEmail_quandoDbDataForValido() {
        var email = assertDoesNotThrow(() -> converter.convertToEntityAttribute("teste@teste.com"));
        assertEquals(umEmail(), email);
    }
}
