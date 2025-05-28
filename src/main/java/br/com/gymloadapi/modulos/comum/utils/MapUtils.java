package br.com.gymloadapi.modulos.comum.utils;

import lombok.experimental.UtilityClass;

import java.util.function.Function;
import java.util.function.Supplier;

@UtilityClass
public class MapUtils {

    public static <C> C mapNullBoolean(boolean value, Supplier<C> function) {
        return value ? function.get() : null;
    }

    public static <T, C> C mapNullWithBackup(T value, Function<T, C> function, C backup) {
        return (value != null) ? function.apply(value) : backup;
    }
}
