package br.com.gymloadapi.modulos.treino.helper;

import br.com.gymloadapi.modulos.comum.enums.ESituacao;
import br.com.gymloadapi.modulos.treino.dto.TreinoImportarRequest;
import br.com.gymloadapi.modulos.treino.dto.TreinoRequest;
import br.com.gymloadapi.modulos.treino.model.Treino;
import br.com.gymloadapi.modulos.treino.model.TreinoCompartilhamento;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umaListaDeExercicios;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;

@UtilityClass
public class TreinoHelper {

    public static TreinoRequest umTreinoRequest() {
        return new TreinoRequest("Um Treino", List.of(1, 2));
    }

    public static TreinoRequest outroTreinoRequest() {
        return new TreinoRequest("Outro Treino", List.of(3, 4));
    }

    public static TreinoRequest maisUmTreinoRequest() {
        return new TreinoRequest("Outro Treino", List.of(1, 2));
    }

    public static Treino umTreino(ESituacao situacao) {
        return Treino.builder()
            .id(1)
            .nome("Um Treino")
            .dataCadastro(LocalDate.of(2025, 4, 6))
            .usuario(umUsuarioAdmin())
            .exercicios(umaListaDeExercicios())
            .situacao(situacao)
            .importado(false)
            .build();
    }

    public static Treino umTreinoImportado(ESituacao situacao) {
        return Treino.builder()
            .id(2)
            .nome("Treino Importado")
            .dataCadastro(LocalDate.of(2025, 4, 6))
            .usuario(umUsuarioAdmin())
            .exercicios(umaListaDeExercicios())
            .situacao(situacao)
            .importado(true)
            .build();
    }

    public static TreinoCompartilhamento umTreinoCompartilhamento() {
        return TreinoCompartilhamento.builder()
            .id(1)
            .token("550e8400-e29b-41d4-a716-446655440000")
            .codigo("A3K9XZ72")
            .nomeTreino("Um Treino")
            .exerciciosIds("1,2")
            .dataCriacao(LocalDateTime.of(2025, 4, 6, 10, 0))
            .dataExpiracao(LocalDateTime.of(2099, 12, 31, 23, 59))
            .usuario(umUsuarioAdmin())
            .build();
    }

    public static TreinoCompartilhamento umTreinoCompartilhamentoExpirado() {
        return TreinoCompartilhamento.builder()
            .id(2)
            .token("expired-token-uuid")
            .codigo("EXPIRED1")
            .nomeTreino("Um Treino")
            .exerciciosIds("1,2")
            .dataCriacao(LocalDateTime.of(2020, 1, 1, 0, 0))
            .dataExpiracao(LocalDateTime.of(2020, 1, 31, 23, 59))
            .usuario(umUsuarioAdmin())
            .build();
    }

    public static TreinoImportarRequest umTreinoImportarRequest() {
        return new TreinoImportarRequest("A3K9XZ72", "Um Treino Importado");
    }
}
