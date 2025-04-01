package br.com.gymloadapi.modulos.comum.service;

import br.com.gymloadapi.modulos.comum.dto.SelectResponse;
import br.com.gymloadapi.modulos.comum.enums.ETipoExercicio;
import br.com.gymloadapi.modulos.comum.enums.ETipoPegada;
import br.com.gymloadapi.modulos.comum.enums.EUnidadePeso;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class EnumService {

    public List<SelectResponse> getTiposExerciciosSelect() {
        return Arrays.stream(ETipoExercicio.values())
            .map(tipoExercicio -> new SelectResponse(tipoExercicio.name(), tipoExercicio.getDescricao()))
            .toList();
    }

    public List<SelectResponse> getTiposPegadasSelect() {
        return Arrays.stream(ETipoPegada.values())
            .map(tipoPegada -> new SelectResponse(tipoPegada.name(), tipoPegada.getDescricao()))
            .toList();
    }

    public List<SelectResponse> getUnidadesPesosSelect() {
        return Arrays.stream(EUnidadePeso.values())
            .map(unidadePeso -> new SelectResponse(unidadePeso.name(), unidadePeso.getDescricao()))
            .toList();
    }
}
