package br.com.gymloadapi.modulos.registroatividade.mapper;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento.HALTER;
import static br.com.gymloadapi.modulos.comum.enums.EUnidadePeso.KG;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.*;
import static br.com.gymloadapi.modulos.registroatividade.helper.RegistroAtividadeHelper.*;
import static br.com.gymloadapi.modulos.registroatividade.registroaerobico.helper.RegistroMusculacaoHelper.outroRegistroAerobico;
import static br.com.gymloadapi.modulos.registroatividade.registroaerobico.helper.RegistroMusculacaoHelper.umRegistroAerobico;
import static br.com.gymloadapi.modulos.registroatividade.registrocalistenia.helper.RegistroCalisteniaHelper.maisUmRegistroCalistenia;
import static br.com.gymloadapi.modulos.registroatividade.registrocalistenia.helper.RegistroCalisteniaHelper.umRegistroCalistenia;
import static br.com.gymloadapi.modulos.registroatividade.registromusculacao.helper.RegistroMusculacaoHelper.maisUmRegistroMusculacao;
import static br.com.gymloadapi.modulos.registroatividade.registromusculacao.helper.RegistroMusculacaoHelper.umRegistroMusculacao;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuario;
import static org.junit.jupiter.api.Assertions.*;

class RegistroAtividadeMapperTest {

    private final RegistroAtividadeMapper mapper = new RegistroAtividadeMapperImpl();

    @Test
    void mapToRegistroMusculacao_deveRetornarRegistroMusculacao_quandoSolicitado() {
        var registroMusculacao = mapper.mapToRegistroMusculacao(umRegistroAtividadeRequestParaMusculacao(),
            umExercicioMusculacao(1), umUsuario());

        assertAll(
            () -> assertNull(registroMusculacao.getId()),
            () -> assertEquals(22.5, registroMusculacao.getPeso()),
            () -> assertEquals(KG, registroMusculacao.getUnidadePeso()),
            () -> assertEquals(12, registroMusculacao.getQtdRepeticoes()),
            () -> assertEquals(4, registroMusculacao.getQtdSeries()),
            () -> assertNotNull(registroMusculacao.getDataCadastro()),
            () -> assertEquals("Observacao", registroMusculacao.getObservacao()),
            () -> assertEquals(1, registroMusculacao.getExercicio().getId()),
            () -> assertEquals(2, registroMusculacao.getUsuario().getId())
        );
    }

    @Test
    void mapToRegistroAerobico_deveRetornarRegistroAerobico_quandoSolicitado() {
        var registroAerobico = mapper.mapToRegistroAerobico(umRegistroAtividadeRequestParaAerobico(),
            umExercicioAerobico(2), umUsuario());

        assertAll(
            () -> assertNull(registroAerobico.getId()),
            () -> assertEquals(20.0, registroAerobico.getDistancia()),
            () -> assertEquals(1.5, registroAerobico.getDuracao()),
            () -> assertNotNull(registroAerobico.getDataCadastro()),
            () -> assertEquals("Observacao", registroAerobico.getObservacao()),
            () -> assertEquals(2, registroAerobico.getExercicio().getId()),
            () -> assertEquals(2, registroAerobico.getUsuario().getId())
        );
    }

    @Test
    void mapToRegistroCalistenia_deveRetornarRegistroCalisenia_quandoSolicitado() {
        var registroCalistenia = mapper.mapToRegistroCalistenia(umRegistroAtividadeRequestParaCalistenia(),
            umExercicioCalistenia(1), umUsuario());

        assertAll(
            () -> assertNull(registroCalistenia.getId()),
            () -> assertNull(registroCalistenia.getPesoAdicional()),
            () -> assertNull(registroCalistenia.getUnidadePeso()),
            () -> assertEquals(20, registroCalistenia.getQtdRepeticoes()),
            () -> assertEquals(5, registroCalistenia.getQtdSeries()),
            () -> assertNotNull(registroCalistenia.getDataCadastro()),
            () -> assertEquals("Observacao", registroCalistenia.getObservacao()),
            () -> assertEquals(1, registroCalistenia.getExercicio().getId()),
            () -> assertEquals(2, registroCalistenia.getUsuario().getId())
        );
    }

    @Test
    void mapToHistoricoRegistroAtividadeMusculacaoResponse_deveRetornarHistoricoDeAtividadeMusculacao_quandoSolicitado() {
        var historicoRegistroAtividadeResponse = mapper.mapToHistoricoRegistroAtividadeMusculacaoResponse(umRegistroMusculacao());

        assertAll(
            () -> assertEquals(1, historicoRegistroAtividadeResponse.id()),
            () -> assertEquals("SUPINO RETO", historicoRegistroAtividadeResponse.exercicioNome()),
            () -> assertEquals("Observacao", historicoRegistroAtividadeResponse.observacao()),
            () -> assertEquals(LocalDate.of(2025, 4, 4), historicoRegistroAtividadeResponse.dataCadastro()),
            () -> assertEquals("22.5 (KG)", historicoRegistroAtividadeResponse.carga()),
            () -> assertEquals(22.5, historicoRegistroAtividadeResponse.peso()),
            () -> assertEquals(KG, historicoRegistroAtividadeResponse.unidadePeso()),
            () -> assertEquals(HALTER, historicoRegistroAtividadeResponse.tipoEquipamento()),
            () -> assertEquals("Peitoral", historicoRegistroAtividadeResponse.grupoMuscularNome()),
            () -> assertEquals(12, historicoRegistroAtividadeResponse.qtdRepeticoes()),
            () -> assertEquals(4, historicoRegistroAtividadeResponse.qtdSeries()),
            () -> assertNull(historicoRegistroAtividadeResponse.distancia()),
            () -> assertNull(historicoRegistroAtividadeResponse.duracao()),
            () -> assertNull(historicoRegistroAtividadeResponse.velocidadeMedia())
        );
    }

    @Test
    void mapToHistoricoRegistroAtividadeAerobicoResponse_deveRetornarHistoricoDeAtividadeAerobico_quandoSolicitado() {
        var historicoRegistroAtividadeResponse = mapper.mapToHistoricoRegistroAtividadeAerobicoResponse(umRegistroAerobico());

        assertAll(
            () -> assertEquals(1, historicoRegistroAtividadeResponse.id()),
            () -> assertEquals("Esteira", historicoRegistroAtividadeResponse.exercicioNome()),
            () -> assertEquals("Observacao", historicoRegistroAtividadeResponse.observacao()),
            () -> assertEquals(LocalDate.of(2025, 4, 14), historicoRegistroAtividadeResponse.dataCadastro()),
            () -> assertNull(historicoRegistroAtividadeResponse.carga()),
            () -> assertNull(historicoRegistroAtividadeResponse.tipoEquipamento()),
            () -> assertNull(historicoRegistroAtividadeResponse.grupoMuscularNome()),
            () -> assertNull(historicoRegistroAtividadeResponse.qtdRepeticoes()),
            () -> assertNull(historicoRegistroAtividadeResponse.qtdSeries()),
            () -> assertEquals(22.6, historicoRegistroAtividadeResponse.distancia()),
            () -> assertEquals(1.33, historicoRegistroAtividadeResponse.duracao()),
            () -> assertEquals("16.99 km/h", historicoRegistroAtividadeResponse.velocidadeMedia())
        );
    }

    @Test
    void mapToHistoricoRegistroAtividadeCalisteniaResponse_deveRetornarHistoricoDeAtividadeCalistenia_quandoSolicitado() {
        var historicoRegistroAtividadeResponse = mapper.mapToHistoricoRegistroAtividadeCalisteniaResponse(umRegistroCalistenia());

        assertAll(
            () -> assertEquals(1, historicoRegistroAtividadeResponse.id()),
            () -> assertEquals("Abdominal Supra", historicoRegistroAtividadeResponse.exercicioNome()),
            () -> assertEquals("Observacao", historicoRegistroAtividadeResponse.observacao()),
            () -> assertEquals(LocalDate.of(2025, 4, 4), historicoRegistroAtividadeResponse.dataCadastro()),
            () -> assertNull(historicoRegistroAtividadeResponse.carga()),
            () -> assertNull(historicoRegistroAtividadeResponse.peso()),
            () -> assertNull(historicoRegistroAtividadeResponse.unidadePeso()),
            () -> assertNull(historicoRegistroAtividadeResponse.tipoEquipamento()),
            () -> assertEquals("Abdomen", historicoRegistroAtividadeResponse.grupoMuscularNome()),
            () -> assertEquals(30, historicoRegistroAtividadeResponse.qtdRepeticoes()),
            () -> assertEquals(4, historicoRegistroAtividadeResponse.qtdSeries()),
            () -> assertNull(historicoRegistroAtividadeResponse.distancia()),
            () -> assertNull(historicoRegistroAtividadeResponse.duracao()),
            () -> assertNull(historicoRegistroAtividadeResponse.velocidadeMedia())
        );
    }

    @Test
    void editarRegistroMusculacao_deveAlterarOsDadosDoRegistroMusculacao_quandoSolicitado() {
        var registroMusculacao = maisUmRegistroMusculacao();
        mapper.editarRegistroMusculacao(umRegistroAtividadeRequestParaMusculacao(), registroMusculacao);

        assertAll(
            () -> assertEquals(3, registroMusculacao.getId()),
            () -> assertEquals(22.5, registroMusculacao.getPeso()),
            () -> assertEquals(KG, registroMusculacao.getUnidadePeso()),
            () -> assertEquals(12, registroMusculacao.getQtdRepeticoes()),
            () -> assertEquals(4, registroMusculacao.getQtdSeries()),
            () -> assertNotNull(registroMusculacao.getDataCadastro()),
            () -> assertEquals("Observacao", registroMusculacao.getObservacao()),
            () -> assertEquals(1, registroMusculacao.getExercicio().getId()),
            () -> assertEquals(1, registroMusculacao.getUsuario().getId())
        );
    }

    @Test
    void editarRegistroAerobico_deveAlterarDadosDoRegistroAerobico_quandoSolicitado() {
        var registroAerobico = outroRegistroAerobico();
        mapper.editarRegistroAerobico(umRegistroAtividadeRequestParaAerobico(), registroAerobico);

        assertAll(
            () -> assertEquals(2, registroAerobico.getId()),
            () -> assertEquals(20.0, registroAerobico.getDistancia()),
            () -> assertEquals(1.5, registroAerobico.getDuracao()),
            () -> assertNotNull(registroAerobico.getDataCadastro()),
            () -> assertEquals("Observacao", registroAerobico.getObservacao()),
            () -> assertEquals(1, registroAerobico.getExercicio().getId()),
            () -> assertEquals(2, registroAerobico.getUsuario().getId())
        );
    }

    @Test
    void editarRegistroCalistenia_deveAlterarOsDadosDoRegistroCalistenia_quandoSolicitado() {
        var registroCalistenia = maisUmRegistroCalistenia();
        mapper.editarRegistroCalistenia(umRegistroAtividadeRequestParaCalistenia(), registroCalistenia);

        assertAll(
            () -> assertEquals(3, registroCalistenia.getId()),
            () -> assertEquals(15.2, registroCalistenia.getPesoAdicional()),
            () -> assertEquals(KG, registroCalistenia.getUnidadePeso()),
            () -> assertEquals(20, registroCalistenia.getQtdRepeticoes()),
            () -> assertEquals(5, registroCalistenia.getQtdSeries()),
            () -> assertNotNull(registroCalistenia.getDataCadastro()),
            () -> assertEquals("Observacao", registroCalistenia.getObservacao()),
            () -> assertEquals(1, registroCalistenia.getExercicio().getId()),
            () -> assertEquals(1, registroCalistenia.getUsuario().getId())
        );
    }

    @Test
    void mapToRegistroAtividadeResponse_deveMapearParaRegistroAtividadeResponse_quandoSolicitadoComDadosDeMusculacao() {
        var response = mapper.mapToRegistroAtividadeResponse(1, "20 (KG)", "15 (KG)", null, null);

        assertAll(
            () -> assertEquals(1, response.exercicioId()),
            () -> assertEquals("20 (KG)", response.destaque()),
            () -> assertEquals("15 (KG)", response.ultimoPeso()),
            () -> assertNull(response.ultimaDistancia()),
            () -> assertNull(response.ultimaQtdMaxRepeticoes())
        );
    }

    @Test
    void mapToRegistroAtividadeResponse_deveMapearParaRegistroAtividadeResponse_quandoSolicitadoComDadosDeAerobico() {
        var response = mapper.mapToRegistroAtividadeResponse(2, "20 km", null, "15 km", null);

        assertAll(
            () -> assertEquals(2, response.exercicioId()),
            () -> assertEquals("20 km", response.destaque()),
            () -> assertNull(response.ultimoPeso()),
            () -> assertEquals("15 km", response.ultimaDistancia()),
            () -> assertNull(response.ultimaQtdMaxRepeticoes())
        );
    }

    @Test
    void mapToRegistroAtividadeResponse_deveMapearParaRegistroAtividadeResponse_quandoSolicitadoComDadosDeCalistenia() {
        var response = mapper.mapToRegistroAtividadeResponse(3, "30 reps", null, null, 20);

        assertAll(
            () -> assertEquals(3, response.exercicioId()),
            () -> assertEquals("30 reps", response.destaque()),
            () -> assertNull(response.ultimoPeso()),
            () -> assertNull(response.ultimaDistancia()),
            () -> assertEquals(20, response.ultimaQtdMaxRepeticoes())
        );
    }
}
