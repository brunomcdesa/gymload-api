package br.com.gymloadapi.modulos.dashboard.service;

import br.com.gymloadapi.modulos.dashboard.dto.RecordeRecenteResponse;
import br.com.gymloadapi.modulos.registroatividade.registroaerobico.service.RegistroAerobicoService;
import br.com.gymloadapi.modulos.registroatividade.registrocalistenia.service.RegistroCalisteniaService;
import br.com.gymloadapi.modulos.registroatividade.registromusculacao.service.RegistroMusculacaoService;
import br.com.gymloadapi.modulos.treino.repository.TreinoSessaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import(DashboardServiceTest.TestServiceConfig.class)
class DashboardServiceTest {

    @TestConfiguration
    static class TestServiceConfig {

        @Bean
        public DashboardService dashboardService(TreinoSessaoRepository repository,
                                                 RegistroMusculacaoService registroMusculacaoService,
                                                 RegistroCalisteniaService registroCalisteniaService,
                                                 RegistroAerobicoService registroAerobicoService) {
            return new DashboardService(repository, registroMusculacaoService, registroCalisteniaService,
                registroAerobicoService);
        }
    }

    @Autowired
    private DashboardService service;
    @MockitoBean
    private TreinoSessaoRepository repository;
    @MockitoBean
    private RegistroMusculacaoService registroMusculacaoService;
    @MockitoBean
    private RegistroCalisteniaService registroCalisteniaService;
    @MockitoBean
    private RegistroAerobicoService registroAerobicoService;

    @Test
    void buscarStats_deveCalcularStreakCorretamente_quandoHaDiasConsecutivos() {
        var hoje = LocalDate.now();
        var datas = List.of(hoje, hoje.minusDays(1), hoje.minusDays(2));
        when(repository.findDistinctDatesByUsuarioId(1)).thenReturn(datas);
        when(repository.countSessoesByUsuarioIdBetween(eq(1), any(), any())).thenReturn(3L);
        when(repository.findDistinctDatesByUsuarioIdBetween(eq(1), any(), any())).thenReturn(emptyList());
        when(registroMusculacaoService.buscarRecordePessoalResume(1)).thenReturn(emptyList());
        when(registroCalisteniaService.buscarRecordePessoalResume(1)).thenReturn(emptyList());
        when(registroAerobicoService.buscarRecordePessoalResume(1)).thenReturn(emptyList());

        var stats = service.buscarStats(1);

        assertEquals(3, stats.streak());
        verify(repository).findDistinctDatesByUsuarioId(1);
    }

    @Test
    void buscarStats_deveZerarStreak_quandoHaGapNasDatas() {
        var hoje = LocalDate.now();
        var datas = List.of(hoje, hoje.minusDays(2));
        when(repository.findDistinctDatesByUsuarioId(1)).thenReturn(datas);
        when(repository.countSessoesByUsuarioIdBetween(eq(1), any(), any())).thenReturn(2L);
        when(repository.findDistinctDatesByUsuarioIdBetween(eq(1), any(), any())).thenReturn(emptyList());
        when(registroMusculacaoService.buscarRecordePessoalResume(1)).thenReturn(emptyList());
        when(registroCalisteniaService.buscarRecordePessoalResume(1)).thenReturn(emptyList());
        when(registroAerobicoService.buscarRecordePessoalResume(1)).thenReturn(emptyList());

        var stats = service.buscarStats(1);

        assertEquals(1, stats.streak());
    }

    @Test
    void buscarStats_deveRetornarStreakZero_quandoNaoHaSessoes() {
        when(repository.findDistinctDatesByUsuarioId(1)).thenReturn(emptyList());
        when(repository.countSessoesByUsuarioIdBetween(eq(1), any(), any())).thenReturn(0L);
        when(repository.findDistinctDatesByUsuarioIdBetween(eq(1), any(), any())).thenReturn(emptyList());
        when(registroMusculacaoService.buscarRecordePessoalResume(1)).thenReturn(emptyList());
        when(registroCalisteniaService.buscarRecordePessoalResume(1)).thenReturn(emptyList());
        when(registroAerobicoService.buscarRecordePessoalResume(1)).thenReturn(emptyList());

        var stats = service.buscarStats(1);

        assertEquals(0, stats.streak());
        assertEquals(0L, stats.treinosMes());
    }

    @Test
    void buscarStats_deveRetornarTreinosMesCorreto() {
        when(repository.findDistinctDatesByUsuarioId(1)).thenReturn(emptyList());
        when(repository.countSessoesByUsuarioIdBetween(eq(1), any(), any())).thenReturn(12L);
        when(repository.findDistinctDatesByUsuarioIdBetween(eq(1), any(), any())).thenReturn(emptyList());
        when(registroMusculacaoService.buscarRecordePessoalResume(1)).thenReturn(emptyList());
        when(registroCalisteniaService.buscarRecordePessoalResume(1)).thenReturn(emptyList());
        when(registroAerobicoService.buscarRecordePessoalResume(1)).thenReturn(emptyList());

        var stats = service.buscarStats(1);

        assertEquals(12L, stats.treinosMes());
    }

    @Test
    void buscarStats_deveMarcarDotsDaSemanaCorretamente() {
        var monday = LocalDate.now().with(DayOfWeek.MONDAY);
        var wednesday = monday.plusDays(2);
        when(repository.findDistinctDatesByUsuarioId(1)).thenReturn(emptyList());
        when(repository.countSessoesByUsuarioIdBetween(eq(1), any(), any())).thenReturn(0L);
        when(repository.findDistinctDatesByUsuarioIdBetween(eq(1), any(), any()))
            .thenReturn(List.of(monday, wednesday));
        when(registroMusculacaoService.buscarRecordePessoalResume(1)).thenReturn(emptyList());
        when(registroCalisteniaService.buscarRecordePessoalResume(1)).thenReturn(emptyList());
        when(registroAerobicoService.buscarRecordePessoalResume(1)).thenReturn(emptyList());

        var stats = service.buscarStats(1);

        assertAll(
            () -> assertTrue(stats.diasSemana()[0], "Segunda deve estar marcada"),
            () -> assertFalse(stats.diasSemana()[1], "Terça não deve estar marcada"),
            () -> assertTrue(stats.diasSemana()[2], "Quarta deve estar marcada"),
            () -> assertFalse(stats.diasSemana()[6], "Domingo não deve estar marcado")
        );
    }

    @Test
    void buscarStats_deveRetornarListaVaziaDeRecordesRecentes_quandoNaoHaRegistros() {
        when(repository.findDistinctDatesByUsuarioId(1)).thenReturn(emptyList());
        when(repository.countSessoesByUsuarioIdBetween(eq(1), any(), any())).thenReturn(0L);
        when(repository.findDistinctDatesByUsuarioIdBetween(eq(1), any(), any())).thenReturn(emptyList());
        when(registroMusculacaoService.buscarRecordePessoalResume(1)).thenReturn(emptyList());
        when(registroCalisteniaService.buscarRecordePessoalResume(1)).thenReturn(emptyList());
        when(registroAerobicoService.buscarRecordePessoalResume(1)).thenReturn(emptyList());

        var stats = service.buscarStats(1);

        assertAll(
            () -> assertTrue(stats.recordesRecentes().isEmpty()),
            () -> assertEquals(0, stats.prsEssaSemana())
        );
    }

    @Test
    void buscarStats_deveRetornarAteCincoRecordesRecentes_ordenadosPorDataDecrescente() {
        var segunda = LocalDate.now().with(DayOfWeek.MONDAY);
        var recordes = List.of(
            new RecordeRecenteResponse(1, "Supino", "MUSCULACAO", "100.0 (KG)", segunda, null, null, false),
            new RecordeRecenteResponse(2, "Agachamento", "MUSCULACAO", "120.0 (KG)", segunda.plusDays(1), null, null, false),
            new RecordeRecenteResponse(3, "Esteira", "AEROBICO", "10.0 km", segunda.plusDays(2), null, null, false),
            new RecordeRecenteResponse(4, "Barra Fixa", "CALISTENIA", "20 reps", segunda.plusDays(3), null, null, false),
            new RecordeRecenteResponse(5, "Levantamento", "MUSCULACAO", "80.0 (KG)", segunda.plusDays(4), null, null, false),
            new RecordeRecenteResponse(6, "Rosca", "MUSCULACAO", "30.0 (KG)", segunda.plusDays(5), null, null, false)
        );
        when(repository.findDistinctDatesByUsuarioId(1)).thenReturn(emptyList());
        when(repository.countSessoesByUsuarioIdBetween(eq(1), any(), any())).thenReturn(0L);
        when(repository.findDistinctDatesByUsuarioIdBetween(eq(1), any(), any())).thenReturn(emptyList());
        when(registroMusculacaoService.buscarRecordePessoalResume(1)).thenReturn(recordes);
        when(registroCalisteniaService.buscarRecordePessoalResume(1)).thenReturn(emptyList());
        when(registroAerobicoService.buscarRecordePessoalResume(1)).thenReturn(emptyList());

        var stats = service.buscarStats(1);

        assertAll(
            () -> assertEquals(5, stats.recordesRecentes().size()),
            () -> assertEquals(6, stats.recordesRecentes().getFirst().exercicioId()),
            () -> assertEquals(5, stats.recordesRecentes().get(1).exercicioId()),
            () -> assertEquals(4, stats.recordesRecentes().get(2).exercicioId()),
            () -> assertEquals(3, stats.recordesRecentes().get(3).exercicioId()),
            () -> assertEquals(2, stats.recordesRecentes().getLast().exercicioId())
        );
    }

    @Test
    void buscarStats_deveContarPrsEssaSemanaCorretamente() {
        var monday = LocalDate.now().with(DayOfWeek.MONDAY);
        var recordesSemana = List.of(
            new RecordeRecenteResponse(1, "Supino", "MUSCULACAO", "100.0 (KG)", monday, null, null, false),
            new RecordeRecenteResponse(2, "Agachamento", "MUSCULACAO", "120.0 (KG)", monday.plusDays(1), null, null, false)
        );
        var recordeForaDaSemana = List.of(
            new RecordeRecenteResponse(3, "Esteira", "AEROBICO", "10.0 km", monday.minusDays(7), null, null, false)
        );
        when(repository.findDistinctDatesByUsuarioId(1)).thenReturn(emptyList());
        when(repository.countSessoesByUsuarioIdBetween(eq(1), any(), any())).thenReturn(0L);
        when(repository.findDistinctDatesByUsuarioIdBetween(eq(1), any(), any())).thenReturn(emptyList());
        when(registroMusculacaoService.buscarRecordePessoalResume(1)).thenReturn(recordesSemana);
        when(registroCalisteniaService.buscarRecordePessoalResume(1)).thenReturn(emptyList());
        when(registroAerobicoService.buscarRecordePessoalResume(1)).thenReturn(recordeForaDaSemana);

        var stats = service.buscarStats(1);

        assertEquals(2, stats.prsEssaSemana());
    }
}
