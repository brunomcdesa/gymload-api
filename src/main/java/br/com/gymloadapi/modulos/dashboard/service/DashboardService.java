package br.com.gymloadapi.modulos.dashboard.service;

import br.com.gymloadapi.modulos.dashboard.dto.DashboardStatsResponse;
import br.com.gymloadapi.modulos.treino.repository.TreinoSessaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TreinoSessaoRepository treinoSessaoRepository;

    public DashboardStatsResponse buscarStats(Integer usuarioId) {
        return new DashboardStatsResponse(
            calcularStreak(usuarioId),
            calcularTreinosMes(usuarioId),
            calcularDiasSemana(usuarioId)
        );
    }

    private int calcularStreak(Integer usuarioId) {
        var datas = treinoSessaoRepository.findDistinctDatesByUsuarioId(usuarioId);
        var esperado = new AtomicReference<>(LocalDate.now());
        var streak = new AtomicInteger(0);

        datas.stream()
            .takeWhile(data -> !data.isBefore(esperado.get()))
            .forEach(data -> {
                if (data.equals(esperado.get())) {
                    streak.incrementAndGet();
                    esperado.set(esperado.get().minusDays(1));
                }
            });

        return streak.get();
    }

    private long calcularTreinosMes(Integer usuarioId) {
        var inicio = LocalDate.now().withDayOfMonth(1);
        return treinoSessaoRepository.countSessoesByUsuarioIdBetween(usuarioId, inicio, LocalDate.now());
    }

    private boolean[] calcularDiasSemana(Integer usuarioId) {
        var monday = LocalDate.now().with(DayOfWeek.MONDAY);
        var sunday = monday.with(DayOfWeek.SUNDAY);
        var datasComSessao = treinoSessaoRepository.findDistinctDatesByUsuarioIdBetween(usuarioId, monday, sunday);

        var diasSemana = new boolean[DayOfWeek.values().length];
        datasComSessao.forEach(data -> diasSemana[data.getDayOfWeek().getValue() - 1] = true);
        return diasSemana;
    }
}
