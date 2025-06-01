package br.com.gymloadapi.modulos.registroatividade.dto;

import br.com.gymloadapi.modulos.comum.enums.EUnidadePeso;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Aerobico;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Calistenia;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Musculacao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import jakarta.validation.ConstraintViolationException;

import static br.com.gymloadapi.modulos.comum.enums.EUnidadePeso.KG;
import static br.com.gymloadapi.modulos.comum.utils.MapUtils.mapNull;
import static br.com.gymloadapi.modulos.registroatividade.helper.RegistroAtividadeHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RegistroAtividadeRequestTest {

    @Test
    void aplicarGroupValidators_deveAplicarValidacoesDeMusculacao_quandoReceberRequestComCamposInvalidos() {
        var request = new RegistroAtividadeRequest(null, null, null, null, null,
            null, 20.5, 10.2);

        var exception = assertThrowsExactly(
            ConstraintViolationException.class,
            () -> request.aplicarGroupValidators(Musculacao.class)
        );
        assertThat(exception.getMessage())
            .contains("unidadePeso: é obrigatório.",
                "qtdRepeticoes: é obrigatório.",
                "qtdSeries: é obrigatório.",
                "peso: é obrigatório.",
                "duracao: deve ser nulo",
                "distancia: deve ser nulo");
    }

    @Test
    void aplicarGroupValidators_naoDeveAplicarValidacoesDeMusculacao_quandoReceberRequestComCamposValdos() {
        assertDoesNotThrow(() -> umRegistroAtividadeRequestParaMusculacao().aplicarGroupValidators(Musculacao.class));
    }

    @Test
    void aplicarGroupValidators_deveAplicarValidacoesDeAerobico_quandoReceberRequestComCamposInvalidos() {
        var request = new RegistroAtividadeRequest(null, null, 20.3, KG, 20,
            4, null, null);

        var exception = assertThrowsExactly(
            ConstraintViolationException.class,
            () -> request.aplicarGroupValidators(Aerobico.class)
        );
        assertThat(exception.getMessage())
            .contains("unidadePeso: deve ser nulo",
                "qtdRepeticoes: deve ser nulo",
                "qtdSeries: deve ser nulo",
                "peso: deve ser nulo",
                "duracao: é obrigatório.",
                "distancia: é obrigatório.");
    }

    @Test
    void aplicarGroupValidators_naoDeveAplicarValidacoesDeAerobico_quandoReceberRequestComCamposValdos() {
        assertDoesNotThrow(() -> umRegistroAtividadeRequestParaAerobico().aplicarGroupValidators(Aerobico.class));
    }

    @Test
    void aplicarGroupValidators_deveAplicarValidacoesDeCalistenia_quandoReceberRequestComCamposInvalidos() {
        var request = new RegistroAtividadeRequest(null, null, null, null, null,
            null, 20.5, 10.2);

        var exception = assertThrowsExactly(
            ConstraintViolationException.class,
            () -> request.aplicarGroupValidators(Calistenia.class)
        );
        assertThat(exception.getMessage())
            .contains("qtdRepeticoes: é obrigatório.",
                "qtdSeries: é obrigatório.",
                "duracao: deve ser nulo",
                "distancia: deve ser nulo");
    }

    @Test
    void aplicarGroupValidators_naoDeveAplicarValidacoesDeCalistenia_quandoReceberRequestComCamposValdos() {
        assertDoesNotThrow(() -> umRegistroAtividadeRequestParaCalistenia().aplicarGroupValidators(Calistenia.class));
    }

    @ParameterizedTest
    @SuppressWarnings("LineLength")
    @CsvSource(value = {"1,NULL", "NULL,KG"}, nullValues = "NULL")
    void validarPesoEUnidadePeso_deveLancarException_quandoNaoPossuirPesoOuNaoPossuirUnidadePesoUmSemOOutro(Double peso, String unidadePeso) {
        var registro = umRegistroAtividadeRequestSomenteComPesoEUnidadePeso(peso,
            mapNull(unidadePeso, () -> EUnidadePeso.valueOf(unidadePeso)));

        var exception = assertThrowsExactly(ValidacaoException.class, registro::validarPesoEUnidadePeso);
        assertEquals("O peso e a unidade de peso devem ser informados juntos.", exception.getMessage());
    }

    @Test
    void validarPesoEUnidadePeso_naoDeveLancarException_quandoNaoPossuirPesoENemUnidadePeso() {
        var registro = umRegistroAtividadeRequestComCamposNull();
        assertDoesNotThrow(registro::validarPesoEUnidadePeso);
    }

    @Test
    void validarPesoEUnidadePeso_naoDeveLancarException_quandoPossuirPesoEUnidadePeso() {
        var registro = umRegistroAtividadeRequestSomenteComPesoEUnidadePeso(20.0, KG);
        assertDoesNotThrow(registro::validarPesoEUnidadePeso);
    }
}
