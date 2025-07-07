package br.com.gymloadapi.modulos.comum.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

class ETipoExercicioTest {

    @Test
    void getTiposExerciciosQuePossuemGrupoMuscular_deveRetornarOsTiposDeExerciciosQuePossuemGruposMusculares_quandoSolicitado() {
        assertEquals(List.of(MUSCULACAO, CALISTENIA), ETipoExercicio.getTiposExerciciosQuePossuemGrupoMuscular());
    }

    @ParameterizedTest
    @SuppressWarnings("LineLength")
    @EnumSource(value = ETipoExercicio.class, names = {"AEROBICO"}, mode = EXCLUDE)
    void deveConterGrupoMuscular_deveRetornarTrue_quandoReceberAlgumTipoDeExercicioQuePossuaGrupoMuscular(ETipoExercicio tipoExercicio) {
        assertTrue(deveConterGrupoMuscular(tipoExercicio));
    }

    @Test
    void deveConterGrupoMuscular_deveRetornarFalse_quandoReceberAlgumTipoDeExercicioQueNaoPossuaGrupoMuscular() {
        assertFalse(deveConterGrupoMuscular(AEROBICO));
    }
}
