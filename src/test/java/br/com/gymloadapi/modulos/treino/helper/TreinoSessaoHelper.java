package br.com.gymloadapi.modulos.treino.helper;

import br.com.gymloadapi.modulos.treino.model.TreinoSessao;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;

import static br.com.gymloadapi.modulos.comum.enums.ESituacao.ATIVO;
import static br.com.gymloadapi.modulos.treino.helper.TreinoHelper.umTreino;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;

@UtilityClass
public class TreinoSessaoHelper {

    public static TreinoSessao umaTreinoSessao() {
        return TreinoSessao.builder()
            .id(1)
            .dataSessao(LocalDate.now())
            .treino(umTreino(ATIVO))
            .usuario(umUsuarioAdmin())
            .build();
    }
}
