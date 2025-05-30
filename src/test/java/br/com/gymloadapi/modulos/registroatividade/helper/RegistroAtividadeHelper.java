package br.com.gymloadapi.modulos.registroatividade.helper;

import br.com.gymloadapi.modulos.registroatividade.dto.HistoricoRegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeFiltros;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeRequest;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeResponse;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.List;

import static br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento.HALTER;
import static br.com.gymloadapi.modulos.comum.enums.EUnidadePeso.KG;

@UtilityClass
public class RegistroAtividadeHelper {

    public static RegistroAtividadeRequest umRegistroAtividadeRequestComCamposNull() {
        return new RegistroAtividadeRequest(null, null, null, null, null, null, null, null);
    }

    public static RegistroAtividadeRequest umRegistroAtividadeRequestParaMusculacao() {
        return new RegistroAtividadeRequest(1, "Observacao", 22.5, KG, 12, 4,
            null, null);
    }

    public static RegistroAtividadeFiltros umRegistroAtividadeFiltros() {
        return new RegistroAtividadeFiltros(List.of(1, 2));
    }

    public static RegistroAtividadeRequest umRegistroAtividadeRequestParaAerobico() {
        return new RegistroAtividadeRequest(3, "Observacao", null, null, null, null,
            20.0, 1.5);
    }

    public static RegistroAtividadeResponse umRegistroAtividadeResponseComDadosDeMusculacao() {
        return new RegistroAtividadeResponse(1, "22.5 (KG)", "20.0 (KG)", null);
    }

    public static RegistroAtividadeResponse umRegistroAtividadeResponseComDadosDeAerobico() {
        return new RegistroAtividadeResponse(2, "22.5 KM", null, "11,25 KM");
    }

    public static HistoricoRegistroAtividadeResponse umHistoricoRegistroAtividadeResponseDeMusculacao() {
        return new HistoricoRegistroAtividadeResponse(1, "SUPINO RETO", "Observacao",
            LocalDate.of(2025, 4, 4), "22.5 (KG)", 22.5, KG, HALTER, "Peitoral", 12,
            4, null, null, null
        );
    }

    public static HistoricoRegistroAtividadeResponse umHistoricoRegistroAtividadeResponseDeAerobico() {
        return new HistoricoRegistroAtividadeResponse(2, "ESTEIRA", "Observacao",
            LocalDate.of(2025, 4, 4), null,  null, null, null, null,
            null, null, 22.5, 2.0, "11,25 km/h");
    }
}
