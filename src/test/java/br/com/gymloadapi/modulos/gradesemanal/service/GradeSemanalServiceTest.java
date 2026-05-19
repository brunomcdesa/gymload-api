package br.com.gymloadapi.modulos.gradesemanal.service;

import br.com.gymloadapi.modulos.gradesemanal.mapper.GradeSemanalMapper;
import br.com.gymloadapi.modulos.gradesemanal.mapper.GradeSemanalMapperImpl;
import br.com.gymloadapi.modulos.gradesemanal.model.GradeSemanal;
import br.com.gymloadapi.modulos.gradesemanal.repository.GradeSemanalRepository;
import br.com.gymloadapi.modulos.treino.repository.TreinoSessaoRepository;
import br.com.gymloadapi.modulos.treino.service.TreinoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static br.com.gymloadapi.modulos.comum.enums.EAcao.CADASTRO;
import static br.com.gymloadapi.modulos.comum.enums.EAcao.EDICAO;
import static br.com.gymloadapi.modulos.comum.enums.EAcao.EXCLUSAO;
import static br.com.gymloadapi.modulos.comum.enums.EDiaSemana.QUARTA;
import static br.com.gymloadapi.modulos.comum.enums.EDiaSemana.SEGUNDA;
import static br.com.gymloadapi.modulos.comum.enums.ESituacao.ATIVO;
import static br.com.gymloadapi.modulos.gradesemanal.helper.GradeSemanalHelper.umGradeSemanalRequest;
import static br.com.gymloadapi.modulos.gradesemanal.helper.GradeSemanalHelper.umaGradeSemanal;
import static br.com.gymloadapi.modulos.gradesemanal.helper.GradeSemanalHelper.umaGradeSemanalInativa;
import static br.com.gymloadapi.modulos.gradesemanal.helper.GradeSemanalHelper.umaListaDeGradesSemanais;
import static br.com.gymloadapi.modulos.treino.helper.TreinoHelper.umTreino;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GradeSemanalServiceTest {

    private GradeSemanalService service;
    private final GradeSemanalMapper mapper = new GradeSemanalMapperImpl();

    @Mock
    private GradeSemanalRepository repository;
    @Mock
    private GradeSemanalHistoricoService historicoService;
    @Mock
    private TreinoService treinoService;
    @Mock
    private TreinoSessaoRepository treinoSessaoRepository;
    @Captor
    private ArgumentCaptor<GradeSemanal> captor;

    @BeforeEach
    void setUp() {
        service = new GradeSemanalService(repository, mapper, historicoService, treinoService, treinoSessaoRepository);
    }

    @Test
    void buscarHoje_deveRetornarResponse_quandoExistirGradeAtivaENaoConcluida() {
        var diaSemanaHoje = br.com.gymloadapi.modulos.comum.enums.EDiaSemana
            .fromDayOfWeek(LocalDate.now().getDayOfWeek());
        when(repository.findByUsuarioIdAndDiaSemana(1, diaSemanaHoje))
            .thenReturn(Optional.of(umaGradeSemanal()));
        when(treinoSessaoRepository.existsByUsuarioIdAndTreinoIdAndDataSessao(1, 1, LocalDate.now()))
            .thenReturn(false);

        var response = service.buscarHoje(1);

        assertTrue(response.isPresent());
        assertAll(
            () -> assertEquals(1, response.get().treinoId()),
            () -> assertEquals("Um Treino", response.get().treinoNome()),
            () -> assertFalse(response.get().concluidoHoje())
        );
    }

    @Test
    void buscarHoje_deveRetornarConcluidoTrue_quandoExistirSessaoHoje() {
        var diaSemanaHoje = br.com.gymloadapi.modulos.comum.enums.EDiaSemana
            .fromDayOfWeek(LocalDate.now().getDayOfWeek());
        when(repository.findByUsuarioIdAndDiaSemana(1, diaSemanaHoje))
            .thenReturn(Optional.of(umaGradeSemanal()));
        when(treinoSessaoRepository.existsByUsuarioIdAndTreinoIdAndDataSessao(1, 1, LocalDate.now()))
            .thenReturn(true);

        var response = service.buscarHoje(1);

        assertTrue(response.isPresent());
        assertTrue(response.get().concluidoHoje());
    }

    @Test
    void buscarHoje_deveRetornarVazio_quandoTreinoEstiverInativo() {
        var diaSemanaHoje = br.com.gymloadapi.modulos.comum.enums.EDiaSemana
            .fromDayOfWeek(LocalDate.now().getDayOfWeek());
        when(repository.findByUsuarioIdAndDiaSemana(1, diaSemanaHoje))
            .thenReturn(Optional.of(umaGradeSemanalInativa()));

        var response = service.buscarHoje(1);

        assertTrue(response.isEmpty());
        verifyNoInteractions(treinoSessaoRepository);
    }

    @Test
    void buscarHoje_deveRetornarVazio_quandoNaoExistirGradeParaHoje() {
        var diaSemanaHoje = br.com.gymloadapi.modulos.comum.enums.EDiaSemana
            .fromDayOfWeek(LocalDate.now().getDayOfWeek());
        when(repository.findByUsuarioIdAndDiaSemana(1, diaSemanaHoje))
            .thenReturn(Optional.empty());

        assertTrue(service.buscarHoje(1).isEmpty());
        verifyNoInteractions(treinoSessaoRepository);
    }

    @Test
    void listar_deveRetornarSomenteGradesComTreinoAtivo_quandoSolicitado() {
        when(repository.findByUsuarioId(1)).thenReturn(umaListaDeGradesSemanais());

        var lista = service.listar(1);

        assertEquals(1, lista.size());
        assertAll(
            () -> assertEquals(SEGUNDA, lista.getFirst().diaSemana()),
            () -> assertEquals(1, lista.getFirst().treinoId())
        );
    }

    @Test
    void salvar_deveCriarNovaGrade_quandoNaoExistirParaODia() {
        var request = umGradeSemanalRequest();
        var usuario = umUsuarioAdmin();
        when(treinoService.findByIdAndUsuarioId(1, 1)).thenReturn(umTreino(ATIVO));
        when(repository.findByUsuarioIdAndDiaSemana(1, SEGUNDA)).thenReturn(Optional.empty());

        service.salvar(SEGUNDA, request, usuario);

        verify(repository).save(captor.capture());
        verify(historicoService).salvar(any(GradeSemanal.class), eq(1), eq(CADASTRO));

        var grade = captor.getValue();
        assertAll(
            () -> assertEquals(SEGUNDA, grade.getDiaSemana()),
            () -> assertEquals(1, grade.getTreino().getId()),
            () -> assertEquals(1, grade.getUsuario().getId())
        );
    }

    @Test
    void salvar_deveAtualizarTreinoDaGrade_quandoExistirParaODia() {
        var request = new br.com.gymloadapi.modulos.gradesemanal.dto.GradeSemanalRequest(2);
        var usuario = umUsuarioAdmin();
        var novoTreino = br.com.gymloadapi.modulos.treino.helper.TreinoHelper.umTreinoImportado(ATIVO);
        var gradeExistente = umaGradeSemanal();
        when(treinoService.findByIdAndUsuarioId(2, 1)).thenReturn(novoTreino);
        when(repository.findByUsuarioIdAndDiaSemana(1, SEGUNDA)).thenReturn(Optional.of(gradeExistente));

        service.salvar(SEGUNDA, request, usuario);

        verify(repository).save(gradeExistente);
        verify(historicoService).salvar(gradeExistente, 1, EDICAO);
        assertEquals(novoTreino, gradeExistente.getTreino());
    }

    @Test
    void remover_deveDeletarGrade_quandoExistir() {
        var grade = umaGradeSemanal();
        when(repository.findByUsuarioIdAndDiaSemana(1, SEGUNDA)).thenReturn(Optional.of(grade));

        service.remover(SEGUNDA, 1);

        verify(repository).delete(grade);
        verify(historicoService).salvar(grade, 1, EXCLUSAO);
    }

    @Test
    void remover_naoDeveFazerNada_quandoNaoExistirGradeParaODia() {
        when(repository.findByUsuarioIdAndDiaSemana(1, QUARTA)).thenReturn(Optional.empty());

        service.remover(QUARTA, 1);

        verify(repository, never()).delete(any(GradeSemanal.class));
        verifyNoInteractions(historicoService);
    }

    @Test
    void salvar_deveBuscarTreinoComOwnership_quandoSolicitado() {
        when(treinoService.findByIdAndUsuarioId(anyInt(), anyInt())).thenReturn(umTreino(ATIVO));
        when(repository.findByUsuarioIdAndDiaSemana(anyInt(), any())).thenReturn(Optional.empty());

        service.salvar(SEGUNDA, umGradeSemanalRequest(), umUsuarioAdmin());

        verify(treinoService).findByIdAndUsuarioId(1, 1);
    }
}
