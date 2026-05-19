package br.com.gymloadapi.modulos.comum.enums;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EDiaSemanaTest {

    @ParameterizedTest
    @CsvSource({
        "MONDAY,SEGUNDA",
        "TUESDAY,TERCA",
        "WEDNESDAY,QUARTA",
        "THURSDAY,QUINTA",
        "FRIDAY,SEXTA",
        "SATURDAY,SABADO",
        "SUNDAY,DOMINGO"
    })
    void fromDayOfWeek_deveConverterCorretamente_quandoSolicitado(DayOfWeek dayOfWeek, EDiaSemana esperado) {
        assertEquals(esperado, EDiaSemana.fromDayOfWeek(dayOfWeek));
    }
}
