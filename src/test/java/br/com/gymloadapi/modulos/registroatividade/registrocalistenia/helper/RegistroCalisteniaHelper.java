package br.com.gymloadapi.modulos.registroatividade.registrocalistenia.helper;

import br.com.gymloadapi.modulos.registroatividade.registrocalistenia.model.RegistroCalistenia;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.List;

import static br.com.gymloadapi.modulos.comum.enums.EUnidadePeso.KG;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioCalistenia;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;

@UtilityClass
public class RegistroCalisteniaHelper {

    public static RegistroCalistenia umRegistroCalistenia() {
        return RegistroCalistenia.builder()
            .id(1)
            .qtdRepeticoes(30)
            .dataCadastro(LocalDate.of(2025, 4, 4))
            .qtdSeries(4)
            .observacao("Observacao")
            .exercicio(umExercicioCalistenia(1))
            .usuario(umUsuarioAdmin())
            .build();
    }

    public static RegistroCalistenia outroRegistroCalistenia() {
        return RegistroCalistenia.builder()
            .id(2)
            .pesoAdicional(10.0)
            .unidadePeso(KG)
            .qtdRepeticoes(15)
            .dataCadastro(LocalDate.of(2025, 4, 5))
            .qtdSeries(5)
            .observacao("Observacao")
            .exercicio(umExercicioCalistenia(1))
            .usuario(umUsuarioAdmin())
            .build();
    }

    public static RegistroCalistenia maisUmRegistroCalistenia() {
        return RegistroCalistenia.builder()
            .id(3)
            .pesoAdicional(15.2)
            .unidadePeso(KG)
            .qtdRepeticoes(20)
            .dataCadastro(LocalDate.of(2025, 4, 6))
            .observacao("Observacao")
            .qtdSeries(4)
            .exercicio(umExercicioCalistenia(1))
            .usuario(umUsuarioAdmin())
            .build();
    }

    public static List<RegistroCalistenia> umaListaRegistroCalistenia() {
        return List.of(umRegistroCalistenia(), outroRegistroCalistenia(), maisUmRegistroCalistenia());
    }
}
