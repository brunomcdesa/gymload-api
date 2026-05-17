package br.com.gymloadapi.modulos.dashboard.service;

import br.com.gymloadapi.modulos.dashboard.dto.DashboardStatsResponse;
import br.com.gymloadapi.modulos.dashboard.dto.RecordeRecenteResponse;
import br.com.gymloadapi.modulos.registroatividade.registroaerobico.service.RegistroAerobicoService;
import br.com.gymloadapi.modulos.registroatividade.registrocalistenia.service.RegistroCalisteniaService;
import br.com.gymloadapi.modulos.registroatividade.registromusculacao.service.RegistroMusculacaoService;
import br.com.gymloadapi.modulos.treino.repository.TreinoSessaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final int LIMITE_RECORDES_RECENTES = 5;

    private final TreinoSessaoRepository treinoSessaoRepository;
    private final RegistroMusculacaoService registroMusculacaoService;
    private final RegistroCalisteniaService registroCalisteniaService;
    private final RegistroAerobicoService registroAerobicoService;

    public DashboardStatsResponse buscarStats(Integer usuarioId) {
        var recordesStats = calcularRecordesPessoais(usuarioId);
        return new DashboardStatsResponse(
            calcularStreak(usuarioId),
            calcularTreinosMes(usuarioId),
            calcularDiasSemana(usuarioId),
            recordesStats.prsEssaSemana(),
            recordesStats.recentes()
        );
    }

    private record RecordesStats(List<RecordeRecenteResponse> recentes, int prsEssaSemana) {
    }

    private RecordesStats calcularRecordesPessoais(Integer usuarioId) {
        var monday = LocalDate.now().with(DayOfWeek.MONDAY);
        var sunday = monday.with(DayOfWeek.SUNDAY);

        var todos = Stream.of(
                registroMusculacaoService.buscarRecordePessoalResume(usuarioId),
                registroCalisteniaService.buscarRecordePessoalResume(usuarioId),
                registroAerobicoService.buscarRecordePessoalResume(usuarioId)
            )
            .flatMap(Collection::stream)
            .sorted(Comparator.comparing(RecordeRecenteResponse::dataPr).reversed())
            .toList();

        var recentes = todos.stream().limit(LIMITE_RECORDES_RECENTES).toList();
        var prsEssaSemana = (int) todos.stream()
            .filter(r -> !r.dataPr().isBefore(monday) && !r.dataPr().isAfter(sunday))
            .count();

        return new RecordesStats(recentes, prsEssaSemana);
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
