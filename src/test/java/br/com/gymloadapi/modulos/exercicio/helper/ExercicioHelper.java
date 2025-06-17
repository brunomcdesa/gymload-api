package br.com.gymloadapi.modulos.exercicio.helper;

import br.com.gymloadapi.modulos.exercicio.dto.ExercicioFiltro;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioRequest;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import lombok.experimental.UtilityClass;

import java.util.List;

import static br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento.HALTER;
import static br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento.MAQUINA;
import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.*;
import static br.com.gymloadapi.modulos.grupomuscular.helper.GrupoMuscularHelper.*;
import static java.util.Collections.emptyList;

@UtilityClass
public class ExercicioHelper {

    public static ExercicioRequest umExercicioMusculacaoRequest() {
        return new ExercicioRequest("SUPINO RETO", MUSCULACAO, "Supino Reto", 1, HALTER);
    }

    public static ExercicioRequest umExercicioAerobicoRequest() {
        return new ExercicioRequest("ESCADA", AEROBICO, "Escada", null, null);
    }

    public static ExercicioRequest umExercicioRequestComCamposInvalidos(String nome) {
        return new ExercicioRequest(nome, null, null, null, null);
    }

    public static ExercicioRequest umExercicioRequestMusculacaoComCamposInvalidos(String nome) {
        return new ExercicioRequest(nome, MUSCULACAO, null, null, null);
    }

    public static ExercicioRequest umExercicioRequestAerobicoComCamposInvalidos(String nome) {
        return new ExercicioRequest(nome, AEROBICO, null, 1, HALTER);
    }

    public static Exercicio umExercicioMusculacao(Integer id) {
        return Exercicio.builder()
            .id(id)
            .nome("SUPINO RETO")
            .descricao("Supino Reto")
            .tipoEquipamento(HALTER)
            .tipoExercicio(MUSCULACAO)
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

    public static Exercicio umExercicioCalistenia(Integer id) {
        return Exercicio.builder()
            .id(id)
            .nome("Abdominal Supra")
            .descricao("ABDOMINAL SUPRA")
            .tipoExercicio(CALISTENIA)
            .treinos(emptyList())
            .grupoMuscular(umGrupoMuscularAbdomen())
            .build();
    }

    public static List<Exercicio> umaListaDeExercicios() {
        return List.of(umExercicioMusculacao(1), outroExercicioMusculacao(2));
    }

    public static List<Exercicio> outraListaDeExercicios() {
        return List.of(umExercicioMusculacao(3), outroExercicioMusculacao(4));
    }

    public static List<Exercicio> maisUmaListaDeExercicios() {
        return List.of(umExercicioMusculacao(1), umExercicioAerobico(2), umExercicioCalistenia(3));
    }

    public static ExercicioFiltro umExercicioFiltroVazio() {
        return new ExercicioFiltro(null);
    }

    public static ExercicioFiltro umExercicioFiltro() {
        return new ExercicioFiltro(2);
    }
}
