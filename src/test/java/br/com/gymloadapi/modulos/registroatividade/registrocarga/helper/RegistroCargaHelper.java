package br.com.gymloadapi.modulos.registroatividade.registrocarga.helper;

import br.com.gymloadapi.modulos.registroatividade.registrocarga.dto.HistoricoCargasRequest;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.model.RegistroCarga;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.List;

import static br.com.gymloadapi.modulos.comum.enums.EUnidadePeso.KG;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioMusculacao;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;

@UtilityClass
public class RegistroCargaHelper {

    public static HistoricoCargasRequest umHistoricoCargasRequest() {
        return new HistoricoCargasRequest(22.5, KG, 1, 12, 4);
    }

    public static RegistroCarga umRegistroCarga() {
        return RegistroCarga.builder()
            .id(1)
            .peso(22.5)
            .unidadePeso(KG)
            .qtdRepeticoes(12)
            .dataCadastro(LocalDate.of(2025, 4, 4))
            .qtdSeries(4)
            .observacao("Observacao")
            .exercicio(umExercicioMusculacao(1))
            .usuario(umUsuarioAdmin())
            .build();
    }

    public static RegistroCarga outroRegistroCarga() {
        return RegistroCarga.builder()
            .id(2)
            .peso(25.0)
            .unidadePeso(KG)
            .qtdRepeticoes(8)
            .dataCadastro(LocalDate.of(2025, 4, 5))
            .qtdSeries(2)
            .observacao("Observacao")
            .exercicio(umExercicioMusculacao(1))
            .usuario(umUsuarioAdmin())
            .build();
    }

    public static RegistroCarga maisUmRegistroCarga() {
        return RegistroCarga.builder()
            .id(3)
            .peso(27.2)
            .unidadePeso(KG)
            .qtdRepeticoes(8)
            .dataCadastro(LocalDate.of(2025, 4, 6))
            .observacao("Observacao")
            .qtdSeries(3)
            .exercicio(umExercicioMusculacao(1))
            .usuario(umUsuarioAdmin())
            .build();
    }

    public static List<RegistroCarga> umaListaRegistroCarga() {
        return List.of(umRegistroCarga(), outroRegistroCarga(), maisUmRegistroCarga());
    }
}
