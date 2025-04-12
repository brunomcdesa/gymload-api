package br.com.gymloadapi.modulos.cargas.service;

import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.mapper.HistoricoCargasMapper;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.mapper.HistoricoCargasMapperImpl;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.model.RegistroCarga;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.repository.HistoricoCargasRepository;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.service.RegistroCargaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static br.com.gymloadapi.modulos.comum.enums.EUnidadePeso.KG;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicio;
import static br.com.gymloadapi.modulos.cargas.helper.RegistroCargaHelper.umHistoricoCargasRequest;
import static br.com.gymloadapi.modulos.cargas.helper.RegistroCargaHelper.umaListaHistoricoCargas;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistroCargaServiceTest {

    private RegistroCargaService service;
    private final HistoricoCargasMapper mapper = new HistoricoCargasMapperImpl();

    @Mock
    private ExercicioService exercicioService;
    @Mock
    private HistoricoCargasRepository repository;
    @Captor
    private ArgumentCaptor<RegistroCarga> historicoCargasCaptor;

    @BeforeEach
    void setUp() {
        service = new RegistroCargaService(exercicioService, repository, mapper);
    }

    @Test
    void salvar_deveSalvarHistoricoCargas_quandoSolicitado() {
        when(exercicioService.findById(1)).thenReturn(umExercicio(1));

        service.salvar(umHistoricoCargasRequest(), umUsuarioAdmin());

        verify(exercicioService).findById(1);
        verify(repository).save(historicoCargasCaptor.capture());

        var historicoCargas = historicoCargasCaptor.getValue();

        assertAll(
            () -> assertEquals(22.5, historicoCargas.getPeso()),
            () -> assertEquals(KG, historicoCargas.getUnidadePeso()),
            () -> assertEquals(12, historicoCargas.getQtdRepeticoes()),
            () -> assertEquals(4, historicoCargas.getQtdSeries()),
            () -> assertEquals("SUPINO RETO", historicoCargas.getExercicio().getNome()),
            () -> assertEquals("Usuario Admin", historicoCargas.getUsuario().getNome())
        );
    }

    @Test
    void buscarUltimoHistoricoCargas_deveRetornarResponseComDadosNull_quandoExercicioNaoPossuirNenhumHistoricoCargas() {
        var usuario = umUsuarioAdmin();

        when(repository.findAllByExercicioIdAndUsuarioId(1, usuario.getId())).thenReturn(emptyList());

        var response = service.buscarUltimoHistoricoCargas(1, usuario.getId());

        assertAll(
            () -> assertNull(response.maiorCarga()),
            () -> assertTrue(response.historicoCargas().isEmpty())
        );

        verify(repository).findAllByExercicioIdAndUsuarioId(1, usuario.getId());
    }

    @Test
    void buscarUltimoHistoricoCargas_deveRetornarMaiorCargaECargasDoUltimoDiaRegistrado_quandoSolicitado() {
        var usuario = umUsuarioAdmin();

        when(repository.findAllByExercicioIdAndUsuarioId(1, usuario.getId())).thenReturn(umaListaHistoricoCargas());

        var response = service.buscarUltimoHistoricoCargas(1, usuario.getId());

        assertAll(
            () -> assertEquals(27.2, response.maiorCarga()),
            () -> assertEquals("27.2 (KG)", response.historicoCargas().getFirst().carga()),
            () -> assertEquals(LocalDate.of(2025, 4, 6), response.historicoCargas().getFirst().dataCadastro()),
            () -> assertEquals(8, response.historicoCargas().getFirst().qtdRepeticoes()),
            () -> assertEquals(3, response.historicoCargas().getFirst().qtdSeries())
        );

        verify(repository).findAllByExercicioIdAndUsuarioId(1, usuario.getId());
    }

    @Test
    void buscarHistoricoCargasCompleto_deveRetornarListaVazia_quandoExercicioNaoPossuirNenhumHistoricoCargas() {
        var usuario = umUsuarioAdmin();

        when(repository.findAllByExercicioIdAndUsuarioId(1, usuario.getId())).thenReturn(emptyList());

        assertTrue(service.buscarHistoricoCargasCompleto(1, usuario.getId()).isEmpty());

        verify(repository).findAllByExercicioIdAndUsuarioId(1, usuario.getId());
    }

    @Test
    void buscarHistoricoCargasCompleto_deveRetornarHistoricoCompleto_quandoExercicioPossuirHistoricoCargas() {
        var usuario = umUsuarioAdmin();

        when(repository.findAllByExercicioIdAndUsuarioId(1, usuario.getId())).thenReturn(umaListaHistoricoCargas());

        var responses = service.buscarHistoricoCargasCompleto(1, usuario.getId());

        assertAll(
            () -> assertEquals("27.2 (KG)", responses.getFirst().carga()),
            () -> assertEquals(LocalDate.of(2025, 4, 6), responses.getFirst().dataCadastro()),
            () -> assertEquals(8, responses.getFirst().qtdRepeticoes()),
            () -> assertEquals(3, responses.getFirst().qtdSeries()),

            () -> assertEquals("25.0 (KG)", responses.get(1).carga()),
            () -> assertEquals(LocalDate.of(2025, 4, 5), responses.get(1).dataCadastro()),
            () -> assertEquals(8, responses.get(1).qtdRepeticoes()),
            () -> assertEquals(2, responses.get(1).qtdSeries()),

            () -> assertEquals("22.5 (KG)", responses.getLast().carga()),
            () -> assertEquals(LocalDate.of(2025, 4, 4), responses.getLast().dataCadastro()),
            () -> assertEquals(12, responses.getLast().qtdRepeticoes()),
            () -> assertEquals(4, responses.getLast().qtdSeries())
        );

        verify(repository).findAllByExercicioIdAndUsuarioId(1, usuario.getId());
    }
}
