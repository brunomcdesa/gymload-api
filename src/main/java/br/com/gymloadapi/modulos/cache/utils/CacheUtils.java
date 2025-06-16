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
    public static final String CACHE_TIPOS_EQUIPAMENTOS_SELECT = "tiposEquipamentosSelect";
    public static final String CACHE_TIPOS_PEGADAS_SELECT = "tiposPegadasSelect";
    public static final String CACHE_UNIDADES_PESOS_SELECT = "unidadesPesosSelect";
    public static final String CACHE_TIPOS_EXERCICIOS_SELECT = "tiposExerciciosSelect";
    public static final String CACHE_TODOS_GRUPOS_MUSCULARES = "todosGruposMusculares";
    public static final String CACHE_TODOS_GRUPOS_MUSCULARES_SELECT = "todosGruposMuscularesSelect";
    public static final String CACHE_GRUPO_MUSCULAR_POR_ID = "todosGrupoMuscularPorId";
    public static final String CACHE_TODOS_TREINOS_ATIVOS_DO_USUARIO = "todosTreinosAtivosDoUsuario";
    public static final String CACHE_TODOS_TREINOS_DO_USUARIO = "todosTreinosDoUsuario";

    public static List<String> getCachesExercicio() {
        return List.of(CACHE_TODOS_EXERCICIOS_FILTRO, CACHE_TODOS_EXERCICIOS_SELECT, CACHE_EXERCICIOS_POR_IDS,
            CACHE_EXERCICIOS_POR_TREINO, CACHE_EXERCICIO_POR_ID);
    }

    public static List<String> getCachesGruposMusculares() {
        return List.of(CACHE_TODOS_GRUPOS_MUSCULARES, CACHE_TODOS_GRUPOS_MUSCULARES_SELECT, CACHE_GRUPO_MUSCULAR_POR_ID);
    }

    public static List<String> getCachesTreinos() {
        return List.of(CACHE_TODOS_TREINOS_ATIVOS_DO_USUARIO, CACHE_TODOS_TREINOS_DO_USUARIO);
    }
}
