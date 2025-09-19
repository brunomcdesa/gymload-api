package br.com.gymloadapi.modulos.tipoVariacao.helper;

import br.com.gymloadapi.modulos.tipoVariacao.dto.TipoVariacaoRequest;
import br.com.gymloadapi.modulos.tipoVariacao.model.TipoVariacao;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class TipoVariacaoHelper {

    public static TipoVariacaoRequest umTipoVariacaoRequestComCamposInvalidos() {
        return new TipoVariacaoRequest(null);
    }

    public static TipoVariacaoRequest umTipoVariacaoRequest() {
        return new TipoVariacaoRequest("Halter");
    }

    public static TipoVariacao umTipoVariacao() {
        return TipoVariacao.builder()
            .id(1)
            .nome("Halter")
            .dataCadastro(LocalDateTime.of(2025, 8, 21, 10, 30))
            .usuarioCadastroId(1)
            .usuarioCadastroNome("Usuario Admin")
            .build();
    }
}
