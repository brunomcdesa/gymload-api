package br.com.gymloadapi.modulos.comum.utils;

import lombok.experimental.UtilityClass;

import java.util.function.Supplier;

@UtilityClass
public class MapUtils {

    public static <C> C mapNullBoolean(boolean value, Supplier<C> function) {
        return value ? function.get() : null;
    }
}
