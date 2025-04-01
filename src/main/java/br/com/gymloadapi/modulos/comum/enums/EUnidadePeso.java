package br.com.gymloadapi.modulos.comum.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EUnidadePeso {

    KG("Quilogramas"),
    LBS("Libras");

    private final String descricao;
}
