package br.com.gymloadapi.modulos.dashboard.service;

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
        public DashboardService dashboardService(TreinoSessaoRepository repository) {
            return new DashboardService(repository);
        }
    }

    @Autowired
    private DashboardService service;
    @MockitoBean
    private TreinoSessaoRepository repository;

    @Test
    void buscarStats_deveCalcularStreakCorretamente_quandoHaDiasConsecutivos() {
        var hoje = LocalDate.now();
        var datas = List.of(hoje, hoje.minusDays(1), hoje.minusDays(2));
        when(repository.findDistinctDatesByUsuarioId(1)).thenReturn(datas);
        when(repository.countSessoesByUsuarioIdBetween(eq(1), any(), any())).thenReturn(3L);
        when(repository.findDistinctDatesByUsuarioIdBetween(eq(1), any(), any())).thenReturn(List.of());

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
        when(repository.findDistinctDatesByUsuarioIdBetween(eq(1), any(), any())).thenReturn(List.of());

        var stats = service.buscarStats(1);

        assertEquals(1, stats.streak());
    }

    @Test
    void buscarStats_deveRetornarStreakZero_quandoNaoHaSessoes() {
        when(repository.findDistinctDatesByUsuarioId(1)).thenReturn(List.of());
        when(repository.countSessoesByUsuarioIdBetween(eq(1), any(), any())).thenReturn(0L);
        when(repository.findDistinctDatesByUsuarioIdBetween(eq(1), any(), any())).thenReturn(List.of());

        var stats = service.buscarStats(1);

        assertEquals(0, stats.streak());
        assertEquals(0L, stats.treinosMes());
    }

    @Test
    void buscarStats_deveRetornarTreinosMesCorreto() {
        when(repository.findDistinctDatesByUsuarioId(1)).thenReturn(List.of());
        when(repository.countSessoesByUsuarioIdBetween(eq(1), any(), any())).thenReturn(12L);
        when(repository.findDistinctDatesByUsuarioIdBetween(eq(1), any(), any())).thenReturn(List.of());

        var stats = service.buscarStats(1);

        assertEquals(12L, stats.treinosMes());
    }

    @Test
    void buscarStats_deveMarcarDotsDaSemanaCorretamente() {
        var monday = LocalDate.now().with(DayOfWeek.MONDAY);
        var wednesday = monday.plusDays(2);
        when(repository.findDistinctDatesByUsuarioId(1)).thenReturn(List.of());
        when(repository.countSessoesByUsuarioIdBetween(eq(1), any(), any())).thenReturn(0L);
        when(repository.findDistinctDatesByUsuarioIdBetween(eq(1), any(), any()))
            .thenReturn(List.of(monday, wednesday));

        var stats = service.buscarStats(1);

        assertAll(
            () -> assertTrue(stats.diasSemana()[0], "Segunda deve estar marcada"),
            () -> assertFalse(stats.diasSemana()[1], "Terça não deve estar marcada"),
            () -> assertTrue(stats.diasSemana()[2], "Quarta deve estar marcada"),
            () -> assertFalse(stats.diasSemana()[6], "Domingo não deve estar marcado")
        );
    }
}
