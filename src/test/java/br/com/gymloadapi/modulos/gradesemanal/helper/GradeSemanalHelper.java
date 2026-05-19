package br.com.gymloadapi.modulos.gradesemanal.helper;

import br.com.gymloadapi.modulos.comum.enums.ESituacao;
import br.com.gymloadapi.modulos.gradesemanal.dto.GradeSemanalRequest;
import br.com.gymloadapi.modulos.gradesemanal.model.GradeSemanal;
import lombok.experimental.UtilityClass;

import java.util.List;

import static br.com.gymloadapi.modulos.comum.enums.EDiaSemana.QUARTA;
import static br.com.gymloadapi.modulos.comum.enums.EDiaSemana.SEGUNDA;
import static br.com.gymloadapi.modulos.treino.helper.TreinoHelper.umTreino;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;

@UtilityClass
public class GradeSemanalHelper {

    public static GradeSemanalRequest umGradeSemanalRequest() {
        return new GradeSemanalRequest(1);
    }

    public static GradeSemanal umaGradeSemanal() {
        return GradeSemanal.builder()
            .id(1)
            .diaSemana(SEGUNDA)
            .treino(umTreino(ESituacao.ATIVO))
            .usuario(umUsuarioAdmin())
            .build();
    }

    public static GradeSemanal umaGradeSemanalInativa() {
        return GradeSemanal.builder()
            .id(2)
            .diaSemana(QUARTA)
            .treino(umTreino(ESituacao.INATIVO))
            .usuario(umUsuarioAdmin())
            .build();
    }

    public static List<GradeSemanal> umaListaDeGradesSemanais() {
        return List.of(umaGradeSemanal(), umaGradeSemanalInativa());
    }
}
