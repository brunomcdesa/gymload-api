package br.com.gymloadapi.modulos.comum.enums;

import java.time.DayOfWeek;

public enum EDiaSemana {

    SEGUNDA,
    TERCA,
    QUARTA,
    QUINTA,
    SEXTA,
    SABADO,
    DOMINGO;

    public static EDiaSemana fromDayOfWeek(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> SEGUNDA;
            case TUESDAY -> TERCA;
            case WEDNESDAY -> QUARTA;
            case THURSDAY -> QUINTA;
            case FRIDAY -> SEXTA;
            case SATURDAY -> SABADO;
            case SUNDAY -> DOMINGO;
        };
    }
}
