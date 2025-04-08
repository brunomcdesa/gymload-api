package br.com.gymloadapi.modulos.treino.helper;

import br.com.gymloadapi.modulos.comum.enums.ESituacao;
import br.com.gymloadapi.modulos.treino.dto.TreinoRequest;
import br.com.gymloadapi.modulos.treino.model.Treino;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.List;

import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umaListaDeExercicios;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;

@UtilityClass
public class TreinoHelper {

    public static TreinoRequest umTreinoRequest() {
        return new TreinoRequest("Um Treino", List.of(1, 2));
    }

    public static TreinoRequest outroTreinoRequest() {
        return new TreinoRequest("Um Treino", List.of(3, 4));
    }

    public static Treino umTreino(ESituacao situacao) {
        return Treino.builder()
            .id(1)
            .nome("Um Treino")
            .dataCadastro(LocalDate.of(2025, 4, 6))
            .usuario(umUsuarioAdmin())
            .exercicios(umaListaDeExercicios())
            .situacao(situacao)
            .build();
    }
}
