package br.com.gymloadapi.modulos.exercicio.helper;

import br.com.gymloadapi.modulos.exercicio.dto.ExercicioFiltro;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioRequest;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import lombok.experimental.UtilityClass;

import java.util.List;

import static br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento.HALTER;
import static br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento.MAQUINA;
import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.AEROBICO;
import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.MUSCULACAO;
import static br.com.gymloadapi.modulos.comum.enums.ETipoPegada.PRONADA;
import static br.com.gymloadapi.modulos.grupomuscular.helper.GrupoMuscularHelper.umGrupoMuscularCostas;
import static br.com.gymloadapi.modulos.grupomuscular.helper.GrupoMuscularHelper.umGrupoMuscularPeitoral;
import static java.util.Collections.emptyList;

@UtilityClass
public class ExercicioHelper {

    public static ExercicioRequest umExercicioMusculacaoRequest() {
        return new ExercicioRequest("SUPINO RETO", MUSCULACAO, "Supino Reto", 1, HALTER, PRONADA);
    }

    public static ExercicioRequest umExercicioAerobicoRequest() {
        return new ExercicioRequest("ESCADA", AEROBICO, "Escada", null, null, null);
    }

    public static ExercicioRequest umExercicioRequestComCamposInvalidos(String nome) {
        return new ExercicioRequest(nome, null, null, null, null, null);
    }

    public static ExercicioRequest umExercicioRequestMusculacaoComCamposInvalidos(String nome) {
        return new ExercicioRequest(nome, MUSCULACAO, null, null, null, null);
    }

    public static ExercicioRequest umExercicioRequestAerobicoComCamposInvalidos(String nome) {
        return new ExercicioRequest(nome, AEROBICO, null, 1, HALTER, PRONADA);
    }

    public static Exercicio umExercicioMusculacao(Integer id) {
        return Exercicio.builder()
            .id(id)
            .nome("SUPINO RETO")
            .descricao("Supino Reto")
            .tipoEquipamento(HALTER)
            .tipoExercicio(MUSCULACAO)
            .tipoPegada(PRONADA)
            .grupoMuscular(umGrupoMuscularPeitoral())
            .treinos(emptyList())
            .build();
    }

    public static Exercicio outroExercicioMusculacao(Integer id) {
        return Exercicio.builder()
            .id(id)
            .nome("PUXADA ALTA")
            .descricao("Puxada Alta")
            .tipoEquipamento(MAQUINA)
            .tipoExercicio(MUSCULACAO)
            .tipoPegada(PRONADA)
            .grupoMuscular(umGrupoMuscularCostas())
            .treinos(emptyList())
            .build();
    }

    public static Exercicio umExercicioAerobico(Integer id) {
        return Exercicio.builder()
            .id(id)
            .nome("Esteira")
            .descricao("ESTEIRA")
            .tipoExercicio(AEROBICO)
            .treinos(emptyList())
            .build();
    }

    public static List<Exercicio> umaListaDeExercicios() {
        return List.of(umExercicioMusculacao(1), outroExercicioMusculacao(2));
    }

    public static List<Exercicio> outraListaDeExercicios() {
        return List.of(umExercicioMusculacao(3), outroExercicioMusculacao(4));
    }

    public static List<Exercicio> maisUmaListaDeExercicios() {
        return List.of(umExercicioMusculacao(1), umExercicioAerobico(2));
    }

    public static ExercicioFiltro umExercicioFiltroVazio() {
        return new ExercicioFiltro(null);
    }

    public static ExercicioFiltro umExercicioFiltro() {
        return new ExercicioFiltro(2);
    }
}
