package br.com.gymloadapi.modulos.exercicio.helper;

import br.com.gymloadapi.modulos.exercicio.dto.ExercicioFiltro;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioRequest;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioVariacaoRequest;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.exercicio.model.ExercicioVariacao;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento.*;
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
        return new ExercicioFiltro(null, null);
    }

    public static ExercicioFiltro umExercicioFiltro() {
        return new ExercicioFiltro(CALISTENIA, 2);
    }

    public static ExercicioVariacaoRequest umExercicioVariacaoRequestComCamposInvalidos() {
        return new ExercicioVariacaoRequest(null, null);
    }

    public static ExercicioVariacaoRequest umExercicioVariacaoRequest() {
        return new ExercicioVariacaoRequest(1, HALTER);
    }

    public static ExercicioVariacao umExercicioVariacao() {
        return ExercicioVariacao.builder()
            .id(1)
            .usuarioCadastroId(1)
            .dataCadastro(LocalDateTime.of(2025, 8, 6, 10, 30))
            .exercicio(umExercicioMusculacao(1))
            .nome("SUPINO RETO - Halter")
            .tipoEquipamento(HALTER)
            .build();
    }

    public static ExercicioVariacao outroExercicioVariacao() {
        return ExercicioVariacao.builder()
            .id(2)
            .usuarioCadastroId(1)
            .dataCadastro(LocalDateTime.of(2025, 8, 6, 11, 30))
            .exercicio(umExercicioMusculacao(1))
            .nome("SUPINO RETO - Barra")
            .tipoEquipamento(BARRA)
            .build();
    }

    public static List<ExercicioVariacao> umaListaExercicioVariacao() {
        return List.of(umExercicioVariacao(), outroExercicioVariacao());
    }
}
