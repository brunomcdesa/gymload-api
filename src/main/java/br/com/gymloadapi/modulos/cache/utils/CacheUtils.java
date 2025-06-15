package br.com.gymloadapi.modulos.cache.utils;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class CacheUtils {

    public static final String CACHE_TODOS_EXERCICIOS_FILTRO = "todosExerciciosFiltro";
    public static final String CACHE_TODOS_EXERCICIOS_SELECT = "todosExerciciosSelect";
    public static final String CACHE_EXERCICIOS_POR_IDS = "exerciciosPorIds";
    public static final String CACHE_EXERCICIOS_POR_TREINO = "exerciciosPorTreino";
    public static final String CACHE_EXERCICIO_POR_ID = "exercicioPorId";

    public static List<String> getCachesExercicio() {
        return List.of(CACHE_TODOS_EXERCICIOS_FILTRO, CACHE_TODOS_EXERCICIOS_SELECT, CACHE_EXERCICIOS_POR_IDS,
            CACHE_EXERCICIOS_POR_TREINO, CACHE_EXERCICIO_POR_ID);
    }
}
