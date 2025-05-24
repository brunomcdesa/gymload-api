package br.com.gymloadapi.modulos.comum.converter;

import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.comum.types.Email;
import org.apache.commons.lang3.StringUtils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EmailConverter implements AttributeConverter<Email, String> {

    @Override
    public String convertToDatabaseColumn(Email email) {
        if (email == null) {
            throw new ValidacaoException("O Email é obrigatório.");
        }

        return email.getValor();
    }

    @Override
    public Email convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData)) {
            throw new ValidacaoException("O Email deve ser obrigatório.");
        }
        return new Email(dbData);
    }
}
