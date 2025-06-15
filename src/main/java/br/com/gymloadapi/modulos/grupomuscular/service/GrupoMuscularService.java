package br.com.gymloadapi.modulos.grupomuscular.service;

import br.com.gymloadapi.modulos.comum.dto.SelectResponse;
import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.grupomuscular.dto.GrupoMuscularRequest;
import br.com.gymloadapi.modulos.grupomuscular.dto.GrupoMuscularResponse;
import br.com.gymloadapi.modulos.grupomuscular.mapper.GrupoMuscularMapper;
import br.com.gymloadapi.modulos.grupomuscular.model.GrupoMuscular;
import br.com.gymloadapi.modulos.grupomuscular.repository.GrupoMuscularRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.gymloadapi.modulos.cache.utils.CacheUtils.*;

@Service
@RequiredArgsConstructor
public class GrupoMuscularService {

    private final GrupoMuscularRepository repository;
    private final GrupoMuscularMapper grupoMuscularMapper;

    @Caching(evict = {
        @CacheEvict(value = CACHE_TODOS_GRUPOS_MUSCULARES, allEntries = true),
        @CacheEvict(value = CACHE_TODOS_GRUPOS_MUSCULARES_SELECT, allEntries = true),
    })
    public void salvar(GrupoMuscularRequest request) {
        repository.save(grupoMuscularMapper.mapToModel(request));
    }

    @Cacheable(value = CACHE_TODOS_GRUPOS_MUSCULARES)
    public List<GrupoMuscularResponse> findAllResponse() {
        return this.findAll().stream()
            .map(grupoMuscularMapper::mapToResponse)
            .toList();
    }

    @Cacheable(value = CACHE_TODOS_GRUPOS_MUSCULARES_SELECT)
    public List<SelectResponse> findAllSelect() {
        return this.findAll().stream()
            .map(grupoMuscularMapper::mapToSelectResponse)
            .toList();
    }

    private List<GrupoMuscular> findAll() {
        return repository.findAll();
    }

    @Cacheable(value = CACHE_GRUPO_MUSCULAR_POR_ID)
    public GrupoMuscular findById(Integer id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Grupo Muscular não encontrado."));
    }
}
