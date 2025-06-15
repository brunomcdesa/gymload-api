package br.com.gymloadapi.modulos.exercicio.service;

import br.com.gymloadapi.modulos.comum.dto.SelectResponse;
import br.com.gymloadapi.modulos.comum.enums.EAcao;
import br.com.gymloadapi.modulos.comum.enums.ETipoExercicio;
import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioFiltro;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioRequest;
import br.com.gymloadapi.modulos.exercicio.dto.ExercicioResponse;
import br.com.gymloadapi.modulos.exercicio.mapper.ExercicioMapper;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.exercicio.repository.ExercicioRepository;
import br.com.gymloadapi.modulos.grupomuscular.service.GrupoMuscularService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.gymloadapi.modulos.cache.utils.CacheUtils.*;
import static br.com.gymloadapi.modulos.comum.enums.EAcao.CADASTRO;
import static br.com.gymloadapi.modulos.comum.enums.EAcao.EDICAO;
import static br.com.gymloadapi.modulos.comum.utils.MapUtils.mapNullBoolean;

@Service
@RequiredArgsConstructor
public class ExercicioService {

    private final ExercicioRepository repository;
    private final ExercicioMapper exercicioMapper;
    private final GrupoMuscularService grupoMuscularService;
    private final ExercicioHistoricoService historicoService;

    @Caching(evict = {
        @CacheEvict(value = CACHE_TODOS_EXERCICIOS_FILTRO, allEntries = true),
        @CacheEvict(value = CACHE_TODOS_EXERCICIOS_SELECT, allEntries = true),
        @CacheEvict(value = CACHE_EXERCICIOS_POR_TREINO, allEntries = true),
        @CacheEvict(value = CACHE_EXERCICIOS_POR_IDS, allEntries = true),
    })
    public void salvar(ExercicioRequest request, Integer usuarioId) {
        request.aplicarGroupValidators();
        var exercicio = exercicioMapper.mapToModel(
            request,
            mapNullBoolean(request.deveConterGrupoMuscular(), () -> grupoMuscularService.findById(request.grupoMuscularId()))
        );

        this.saveComHistorico(exercicio, usuarioId, CADASTRO);
    }

    @Cacheable(value = CACHE_TODOS_EXERCICIOS_FILTRO)
    public List<ExercicioResponse> buscarTodos(ExercicioFiltro filtro) {
        return repository.findAllCompleteByPredicate(filtro.toPredicate()).stream()
            .map(exercicioMapper::mapModelToResponse)
            .toList();
    }

    @Cacheable(value = CACHE_EXERCICIO_POR_ID, key = "#id")
    public Exercicio findById(Integer id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Exercício não encontrado."));
    }

    @Cacheable(value = CACHE_TODOS_EXERCICIOS_SELECT)
    public List<SelectResponse> buscarTodosSelect() {
        return repository.findAllComplete().stream()
            .map(exercicioMapper::mapToSelectResponse)
            .toList();
    }

    @Cacheable(value = CACHE_EXERCICIOS_POR_IDS, key = "#exercicioIds")
    public List<Exercicio> findByIdIn(List<Integer> exercicioIds) {
        return repository.findByIdIn(exercicioIds);
    }

    @Cacheable(value = CACHE_EXERCICIOS_POR_TREINO, key = "#treinoId")
    public List<ExercicioResponse> buscarExerciciosPorTreino(Integer treinoId) {
        return repository.buscarExerciciosPorTreino(treinoId).stream()
            .map(exercicioMapper::mapModelToResponse)
            .toList();
    }

    @Caching(evict = {
        @CacheEvict(value = CACHE_EXERCICIO_POR_ID, key = "#id"),
        @CacheEvict(value = CACHE_TODOS_EXERCICIOS_FILTRO, allEntries = true),
        @CacheEvict(value = CACHE_TODOS_EXERCICIOS_SELECT, allEntries = true),
        @CacheEvict(value = CACHE_EXERCICIOS_POR_TREINO, allEntries = true),
        @CacheEvict(value = CACHE_EXERCICIOS_POR_IDS, allEntries = true)
    })
    public void editar(Integer id, ExercicioRequest request, Integer usuarioId) {
        var exercicio = this.findById(id);
        this.validarAlteracaoTipoExercicio(exercicio, request.tipoExercicio());
        request.aplicarGroupValidators();

        exercicioMapper.editar(request,
            mapNullBoolean(request.deveConterGrupoMuscular(), () -> grupoMuscularService.findById(request.grupoMuscularId())),
            exercicio);
        this.saveComHistorico(exercicio, usuarioId, EDICAO);
    }

    private void saveComHistorico(Exercicio exercicio, Integer usuarioId, EAcao acao) {
        repository.save(exercicio);
        historicoService.salvar(exercicio, usuarioId, acao);
    }

    private void validarAlteracaoTipoExercicio(Exercicio exercicio, ETipoExercicio tipoExercicio) {
        if (exercicio.getTipoExercicio() != tipoExercicio) {
            throw new ValidacaoException("Não é permitido alterar o tipo de exercício.");
        }
    }
}
