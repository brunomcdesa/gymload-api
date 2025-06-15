package br.com.gymloadapi.modulos.comum.service;

import br.com.gymloadapi.modulos.comum.dto.SelectResponse;
import br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento;
import br.com.gymloadapi.modulos.comum.enums.ETipoExercicio;
import br.com.gymloadapi.modulos.comum.enums.ETipoPegada;
import br.com.gymloadapi.modulos.comum.enums.EUnidadePeso;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static br.com.gymloadapi.modulos.cache.utils.CacheUtils.*;

@Service
public class EnumService {

    @Cacheable(value = CACHE_TIPOS_EQUIPAMENTOS_SELECT)
    public List<SelectResponse> getTiposEquipamentosSelect() {
        return Arrays.stream(ETipoEquipamento.values())
            .map(tipoEquipamento -> new SelectResponse(tipoEquipamento.name(), tipoEquipamento.getDescricao()))
            .toList();
    }

    @Cacheable(value = CACHE_TIPOS_PEGADAS_SELECT)
    public List<SelectResponse> getTiposPegadasSelect() {
        return Arrays.stream(ETipoPegada.values())
            .map(tipoPegada -> new SelectResponse(tipoPegada.name(), tipoPegada.getDescricao()))
            .toList();
    }

    @Cacheable(value = CACHE_UNIDADES_PESOS_SELECT)
    public List<SelectResponse> getUnidadesPesosSelect() {
        return Arrays.stream(EUnidadePeso.values())
            .map(unidadePeso -> new SelectResponse(unidadePeso.name(), unidadePeso.name()))
            .toList();
    }

    @Cacheable(value = CACHE_TIPOS_EXERCICIOS_SELECT)
    public List<SelectResponse> getTiposExerciciosSelect() {
        return Arrays.stream(ETipoExercicio.values())
            .map(tipoExercicio -> new SelectResponse(tipoExercicio.name(), tipoExercicio.getDescricao()))
            .toList();
    }
}
