package br.com.gymloadapi.modulos.registroatividade.registroaerobico.helper;

import br.com.gymloadapi.modulos.registroatividade.registroaerobico.model.RegistroAerobico;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.List;

import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioAerobico;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuario;

@UtilityClass
public class RegistroMusculacaoHelper {

    public static RegistroAerobico umRegistroAerobico() {
        return RegistroAerobico.builder()
            .id(1)
            .observacao("Observacao")
            .dataCadastro(LocalDate.of(2025, 4, 14))
            .exercicio(umExercicioAerobico(1))
            .usuario(umUsuario())
            .distancia(22.6)
            .duracao(1.33)
            .build();
    }

    public static RegistroAerobico outroRegistroAerobico() {
        return RegistroAerobico.builder()
            .id(2)
            .observacao("Observacao")
            .dataCadastro(LocalDate.of(2025, 4, 13))
            .exercicio(umExercicioAerobico(1))
            .usuario(umUsuario())
            .distancia(26.6)
            .duracao(1.92)
            .build();
    }

    public static List<RegistroAerobico> umaListaDeRegistrosAerobicos() {
        return List.of(umRegistroAerobico(), outroRegistroAerobico());
    }
}
