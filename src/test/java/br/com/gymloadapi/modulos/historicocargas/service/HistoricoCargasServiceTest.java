package br.com.gymloadapi.modulos.historicocargas.service;

import br.com.gymloadapi.autenticacao.service.AutenticacaoService;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.historicocargas.mapper.HistoricoCargasMapper;
import br.com.gymloadapi.modulos.historicocargas.mapper.HistoricoCargasMapperImpl;
import br.com.gymloadapi.modulos.historicocargas.model.HistoricoCargas;
import br.com.gymloadapi.modulos.historicocargas.repository.HistoricoCargasRepository;
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
import static br.com.gymloadapi.modulos.historicocargas.helper.HistoricoCargasHelper.umHistoricoCargasRequest;
import static br.com.gymloadapi.modulos.historicocargas.helper.HistoricoCargasHelper.umaListaHistoricoCargas;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistoricoCargasServiceTest {

    private HistoricoCargasService service;
    private final HistoricoCargasMapper mapper = new HistoricoCargasMapperImpl();

    @Mock
    private ExercicioService exercicioService;
    @Mock
    private HistoricoCargasRepository repository;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Captor
    private ArgumentCaptor<HistoricoCargas> historicoCargasCaptor;

    @BeforeEach
    void setUp() {
        service = new HistoricoCargasService(exercicioService, repository, autenticacaoService, mapper);
    }

    @Test
    void salvar_deveSalvarHistoricoCargas_quandoSolicitado() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAdmin());
        when(exercicioService.findById(1)).thenReturn(umExercicio(1));

        service.salvar(umHistoricoCargasRequest());

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(exercicioService).findById(1);
        verify(repository).save(historicoCargasCaptor.capture());

        var historicoCargas = historicoCargasCaptor.getValue();

        assertAll(
            () -> assertEquals(22.5, historicoCargas.getCarga()),
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

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuario);
        when(repository.findAllByExercicioIdAndUsuario_Id(1, usuario.getId())).thenReturn(emptyList());

        var response = service.buscarUltimoHistoricoCargas(1);

        assertAll(
            () -> assertNull(response.maiorCarga()),
            () -> assertTrue(response.historicoCargas().isEmpty())
        );

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository).findAllByExercicioIdAndUsuario_Id(1, usuario.getId());
    }

    @Test
    void buscarUltimoHistoricoCargas_deveRetornarMaiorCargaECargasDoUltimoDiaRegistrado_quandoSolicitado() {
        var usuario = umUsuarioAdmin();

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuario);
        when(repository.findAllByExercicioIdAndUsuario_Id(1, usuario.getId())).thenReturn(umaListaHistoricoCargas());

        var response = service.buscarUltimoHistoricoCargas(1);

        assertAll(
            () -> assertEquals(27.2, response.maiorCarga()),
            () -> assertEquals("27.2 (KG)", response.historicoCargas().getFirst().carga()),
            () -> assertEquals(LocalDate.of(2025, 4, 6), response.historicoCargas().getFirst().dataCadastro()),
            () -> assertEquals(8, response.historicoCargas().getFirst().qtdRepeticoes()),
            () -> assertEquals(3, response.historicoCargas().getFirst().qtdSeries())
        );

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository).findAllByExercicioIdAndUsuario_Id(1, usuario.getId());
    }

    @Test
    void buscarHistoricoCargasCompleto_deveRetornarListaVazia_quandoExercicioNaoPossuirNenhumHistoricoCargas() {
        var usuario = umUsuarioAdmin();

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuario);
        when(repository.findAllByExercicioIdAndUsuario_Id(1, usuario.getId())).thenReturn(emptyList());

        assertTrue(service.buscarHistoricoCargasCompleto(1).isEmpty());

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository).findAllByExercicioIdAndUsuario_Id(1, usuario.getId());
    }

    @Test
    void buscarHistoricoCargasCompleto_deveRetornarHistoricoCompleto_quandoExercicioPossuirHistoricoCargas() {
        var usuario = umUsuarioAdmin();

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuario);
        when(repository.findAllByExercicioIdAndUsuario_Id(1, usuario.getId())).thenReturn(umaListaHistoricoCargas());

        var responses = service.buscarHistoricoCargasCompleto(1);

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

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository).findAllByExercicioIdAndUsuario_Id(1, usuario.getId());
    }
}
