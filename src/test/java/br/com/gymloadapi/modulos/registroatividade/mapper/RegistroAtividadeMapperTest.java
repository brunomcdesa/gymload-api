package br.com.gymloadapi.modulos.registroatividade.mapper;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento.HALTER;
import static br.com.gymloadapi.modulos.comum.enums.EUnidadePeso.KG;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioAerobico;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioMusculacao;
import static br.com.gymloadapi.modulos.registroatividade.helper.RegistroAtividadeHelper.umRegistroAtividadeRequestParaAerobico;
import static br.com.gymloadapi.modulos.registroatividade.helper.RegistroAtividadeHelper.umRegistroAtividadeRequestParaMusculacao;
import static br.com.gymloadapi.modulos.registroatividade.registrocardio.helper.RegistroCardioHelper.outroRegistroCardio;
import static br.com.gymloadapi.modulos.registroatividade.registrocardio.helper.RegistroCardioHelper.umRegistroCardio;
import static br.com.gymloadapi.modulos.registroatividade.registrocarga.helper.RegistroCargaHelper.maisUmRegistroCarga;
import static br.com.gymloadapi.modulos.registroatividade.registrocarga.helper.RegistroCargaHelper.umRegistroCarga;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuario;
import static org.junit.jupiter.api.Assertions.*;

class RegistroAtividadeMapperTest {

    private final RegistroAtividadeMapper mapper = new RegistroAtividadeMapperImpl();

    @Test
    void mapToRegistroCarga_deveRetornarRegistroCarga_quandoSolicitado() {
        var registroCarga = mapper.mapToRegistroCarga(umRegistroAtividadeRequestParaMusculacao(), umExercicioMusculacao(1),
            umUsuario());

        assertAll(
            () -> assertNull(registroCarga.getId()),
            () -> assertEquals(22.5, registroCarga.getPeso()),
            () -> assertEquals(KG, registroCarga.getUnidadePeso()),
            () -> assertEquals(12, registroCarga.getQtdRepeticoes()),
            () -> assertEquals(4, registroCarga.getQtdSeries()),
            () -> assertNotNull(registroCarga.getDataCadastro()),
            () -> assertEquals("Observacao", registroCarga.getObservacao()),
            () -> assertEquals(1, registroCarga.getExercicio().getId()),
            () -> assertEquals(2, registroCarga.getUsuario().getId())
        );
    }

    @Test
    void mapToRegistroCardio_deveRetornarRegistroCardio_quandoSolicitado() {
        var registroCardio = mapper.mapToRegistroCardio(umRegistroAtividadeRequestParaAerobico(), umExercicioAerobico(2),
            umUsuario());

        assertAll(
            () -> assertNull(registroCardio.getId()),
            () -> assertEquals(20.0, registroCardio.getDistancia()),
            () -> assertEquals(1.5, registroCardio.getDuracao()),
            () -> assertNotNull(registroCardio.getDataCadastro()),
            () -> assertEquals("Observacao", registroCardio.getObservacao()),
            () -> assertEquals(2, registroCardio.getExercicio().getId()),
            () -> assertEquals(2, registroCardio.getUsuario().getId())
        );
    }

    @Test
    void mapToHistoricoRegistroAtividadeMusculacaoResponse_deveRetornarHistoricoDeAtividadeMusculacao_quandoSolicitado() {
        var historicoRegistroAtividadeResponse = mapper.mapToHistoricoRegistroAtividadeMusculacaoResponse(umRegistroCarga());

        assertAll(
            () -> assertEquals(1, historicoRegistroAtividadeResponse.id()),
            () -> assertEquals("SUPINO RETO", historicoRegistroAtividadeResponse.exercicioNome()),
            () -> assertEquals("Observacao", historicoRegistroAtividadeResponse.observacao()),
            () -> assertEquals(LocalDate.of(2025, 4, 4), historicoRegistroAtividadeResponse.dataCadastro()),
            () -> assertEquals("22.5 (KG)", historicoRegistroAtividadeResponse.carga()),
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
        var historicoRegistroAtividadeResponse = mapper.mapToHistoricoRegistroAtividadeAerobicoResponse(umRegistroCardio());

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
    void editarRegistroCarga_deveAlterarOsDadosDoRegistroCarga_quandoSolicitado() {
        var registroCarga = maisUmRegistroCarga();
        mapper.editarRegistroCarga(umRegistroAtividadeRequestParaMusculacao(), registroCarga);

        assertAll(
            () -> assertEquals(3, registroCarga.getId()),
            () -> assertEquals(22.5, registroCarga.getPeso()),
            () -> assertEquals(KG, registroCarga.getUnidadePeso()),
            () -> assertEquals(12, registroCarga.getQtdRepeticoes()),
            () -> assertEquals(4, registroCarga.getQtdSeries()),
            () -> assertNotNull(registroCarga.getDataCadastro()),
            () -> assertEquals("Observacao", registroCarga.getObservacao()),
            () -> assertEquals(1, registroCarga.getExercicio().getId()),
            () -> assertEquals(1, registroCarga.getUsuario().getId())
        );
    }

    @Test
    void editarRegistroCardio_deveAlterarDadosDoRegistroCardio_quandoSolicitado() {
        var registroCardio = outroRegistroCardio();
        mapper.editarRegistroCardio(umRegistroAtividadeRequestParaAerobico(), registroCardio);

        assertAll(
            () -> assertEquals(2, registroCardio.getId()),
            () -> assertEquals(20.0, registroCardio.getDistancia()),
            () -> assertEquals(1.5, registroCardio.getDuracao()),
            () -> assertNotNull(registroCardio.getDataCadastro()),
            () -> assertEquals("Observacao", registroCardio.getObservacao()),
            () -> assertEquals(1, registroCardio.getExercicio().getId()),
            () -> assertEquals(2, registroCardio.getUsuario().getId())
        );
    }
}
