package br.com.gymloadapi.modulos.grupomuscular.helper;

import br.com.gymloadapi.modulos.grupomuscular.dto.GrupoMuscularRequest;
import br.com.gymloadapi.modulos.grupomuscular.model.GrupoMuscular;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GrupoMuscularHelper {

    public static GrupoMuscularRequest umGrupoMuscularRequest() {
        return new GrupoMuscularRequest("Peitoral", "PEITORAL");
    }

    public static GrupoMuscular umGrupoMuscularPeitoral() {
        return GrupoMuscular.builder()
            .id(1)
            .nome("Peitoral")
            .codigo("PEITORAL")
            .build();
    }

    public static GrupoMuscular umGrupoMuscularCostas() {
        return GrupoMuscular.builder()
            .id(2)
            .nome("Costas")
            .codigo("COSTAS")
            .build();
    }
}
