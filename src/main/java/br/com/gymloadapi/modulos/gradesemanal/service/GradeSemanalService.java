package br.com.gymloadapi.modulos.gradesemanal.service;

import br.com.gymloadapi.modulos.comum.enums.EDiaSemana;
import br.com.gymloadapi.modulos.comum.enums.ESituacao;
import br.com.gymloadapi.modulos.gradesemanal.dto.GradeSemanalHojeResponse;
import br.com.gymloadapi.modulos.gradesemanal.dto.GradeSemanalRequest;
import br.com.gymloadapi.modulos.gradesemanal.dto.GradeSemanalResponse;
import br.com.gymloadapi.modulos.gradesemanal.mapper.GradeSemanalMapper;
import br.com.gymloadapi.modulos.gradesemanal.repository.GradeSemanalRepository;
import br.com.gymloadapi.modulos.treino.repository.TreinoSessaoRepository;
import br.com.gymloadapi.modulos.treino.service.TreinoService;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static br.com.gymloadapi.modulos.comum.enums.EAcao.CADASTRO;
import static br.com.gymloadapi.modulos.comum.enums.EAcao.EDICAO;
import static br.com.gymloadapi.modulos.comum.enums.EAcao.EXCLUSAO;

@Service
@RequiredArgsConstructor
public class GradeSemanalService {

    private final GradeSemanalRepository repository;
    private final GradeSemanalMapper mapper;
    private final GradeSemanalHistoricoService historicoService;
    private final TreinoService treinoService;
    private final TreinoSessaoRepository treinoSessaoRepository;

    public Optional<GradeSemanalHojeResponse> buscarHoje(Integer usuarioId) {
        var diaSemana = EDiaSemana.fromDayOfWeek(LocalDate.now().getDayOfWeek());
        return repository.findByUsuarioIdAndDiaSemana(usuarioId, diaSemana)
            .filter(grade -> grade.getTreino().getSituacao() == ESituacao.ATIVO)
            .map(grade -> {
                var concluido = treinoSessaoRepository.existsByUsuarioIdAndTreinoIdAndDataSessao(
                    usuarioId, grade.getTreino().getId(), LocalDate.now());
                return mapper.mapToHojeResponse(grade, concluido);
            });
    }

    public List<GradeSemanalResponse> listar(Integer usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream()
            .filter(grade -> grade.getTreino().getSituacao() == ESituacao.ATIVO)
            .map(mapper::mapToResponse)
            .toList();
    }

    @Transactional
    public void salvar(EDiaSemana diaSemana, GradeSemanalRequest request, Usuario usuario) {
        var treino = treinoService.findByIdAndUsuarioId(request.treinoId(), usuario.getId());

        repository.findByUsuarioIdAndDiaSemana(usuario.getId(), diaSemana)
            .ifPresentOrElse(
                grade -> {
                    grade.alterarTreino(treino);
                    repository.save(grade);
                    historicoService.salvar(grade, usuario.getId(), EDICAO);
                },
                () -> {
                    var grade = mapper.mapToModel(diaSemana, treino, usuario);
                    repository.save(grade);
                    historicoService.salvar(grade, usuario.getId(), CADASTRO);
                }
            );
    }

    @Transactional
    public void remover(EDiaSemana diaSemana, Integer usuarioId) {
        repository.findByUsuarioIdAndDiaSemana(usuarioId, diaSemana)
            .ifPresent(grade -> {
                repository.delete(grade);
                historicoService.salvar(grade, usuarioId, EXCLUSAO);
            });
    }
}
