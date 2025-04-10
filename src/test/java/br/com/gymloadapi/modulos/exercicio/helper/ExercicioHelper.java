package br.com.gymloadapi.modulos.exercicio.helper;

import br.com.gymloadapi.modulos.exercicio.dto.ExercicioRequest;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import lombok.experimental.UtilityClass;

import java.util.List;

import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.HALTER;
import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.MAQUINA;
import static br.com.gymloadapi.modulos.comum.enums.ETipoPegada.PRONADA;
import static br.com.gymloadapi.modulos.grupomuscular.helper.GrupoMuscularHelper.umGrupoMuscularCostas;
import static br.com.gymloadapi.modulos.grupomuscular.helper.GrupoMuscularHelper.umGrupoMuscularPeitoral;
import static java.util.Collections.emptyList;

@UtilityClass
public class ExercicioHelper {

    public static ExercicioRequest umExercicioRequest() {
        return new ExercicioRequest("SUPINO RETO", "Supino Reto", 1, HALTER, PRONADA);
    }

    public static Exercicio umExercicio(Integer id) {
        return Exercicio.builder()
            .id(id)
            .nome("SUPINO RETO")
            .descricao("Supino Reto")
            .tipoExercicio(HALTER)
            .tipoPegada(PRONADA)
            .grupoMuscular(umGrupoMuscularPeitoral())
            .treinos(emptyList())
            .build();
    }

    public static Exercicio outroExercicio(Integer id) {
        return Exercicio.builder()
            .id(id)
            .nome("PUXADA ALTA")
            .descricao("Puxada Alta")
            .tipoExercicio(MAQUINA)
            .tipoPegada(PRONADA)
            .grupoMuscular(umGrupoMuscularCostas())
            .treinos(emptyList())
            .build();
    }

    public static List<Exercicio> umaListaDeExercicios() {
        return List.of(umExercicio(1), outroExercicio(2));
    }
}
