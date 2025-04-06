package br.com.gymloadapi.modulos.historicocargas.helper;

import br.com.gymloadapi.modulos.historicocargas.dto.HistoricoCargasRequest;
import br.com.gymloadapi.modulos.historicocargas.model.HistoricoCargas;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.List;

import static br.com.gymloadapi.modulos.comum.enums.EUnidadePeso.KG;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicio;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;

@UtilityClass
public class HistoricoCargasHelper {

    public static HistoricoCargasRequest umHistoricoCargasRequest() {
        return new HistoricoCargasRequest(22.5, KG, 1, 12, 4);
    }

    public static HistoricoCargas umHistoricoCargas() {
        return HistoricoCargas.builder()
            .id(1)
            .carga(22.5)
            .unidadePeso(KG)
            .qtdRepeticoes(12)
            .dataCadastro(LocalDate.of(2025, 4, 4))
            .qtdSeries(4)
            .exercicio(umExercicio(1))
            .usuario(umUsuarioAdmin())
            .build();
    }

    public static HistoricoCargas outroHistoricoCargas() {
        return HistoricoCargas.builder()
            .id(2)
            .carga(25.0)
            .unidadePeso(KG)
            .qtdRepeticoes(8)
            .dataCadastro(LocalDate.of(2025, 4, 5))
            .qtdSeries(2)
            .exercicio(umExercicio(1))
            .usuario(umUsuarioAdmin())
            .build();
    }

    public static HistoricoCargas maisUmHistoricoCargas() {
        return HistoricoCargas.builder()
            .id(3)
            .carga(27.2)
            .unidadePeso(KG)
            .qtdRepeticoes(8)
            .dataCadastro(LocalDate.of(2025, 4, 6))
            .qtdSeries(3)
            .exercicio(umExercicio(1))
            .usuario(umUsuarioAdmin())
            .build();
    }

    public static List<HistoricoCargas> umaListaHistoricoCargas() {
        return List.of(
            umHistoricoCargas(), outroHistoricoCargas(), maisUmHistoricoCargas()
        );
    }
}
