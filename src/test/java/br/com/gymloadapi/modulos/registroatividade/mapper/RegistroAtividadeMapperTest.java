package br.com.gymloadapi.modulos.registroatividade.mapper;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento.HALTER;
import static br.com.gymloadapi.modulos.comum.enums.EUnidadePeso.KG;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioAerobico;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioMusculacao;
import static br.com.gymloadapi.modulos.registroatividade.helper.RegistroAtividadeHelper.umRegistroAtividadeRequestParaAerobico;
import static br.com.gymloadapi.modulos.registroatividade.helper.RegistroAtividadeHelper.umRegistroAtividadeRequestParaMusculacao;
import static br.com.gymloadapi.modulos.registroatividade.registroaerobico.helper.RegistroMusculacaoHelper.outroRegistroAerobico;
import static br.com.gymloadapi.modulos.registroatividade.registroaerobico.helper.RegistroMusculacaoHelper.umRegistroAerobico;
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
            () -> assertEquals(HALTER, historicoRegistroAtividadeResponse.tipoExercicio()),
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
            () -> assertNull(historicoRegistroAtividadeResponse.tipoExercicio()),
            () -> assertNull(historicoRegistroAtividadeResponse.grupoMuscularNome()),
            () -> assertNull(historicoRegistroAtividadeResponse.qtdRepeticoes()),
            () -> assertNull(historicoRegistroAtividadeResponse.qtdSeries()),
            () -> assertEquals(22.6, historicoRegistroAtividadeResponse.distancia()),
            () -> assertEquals(1.33, historicoRegistroAtividadeResponse.duracao()),
            () -> assertEquals("16.99 km/h", historicoRegistroAtividadeResponse.velocidadeMedia())
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
}
