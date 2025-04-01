package br.com.gymloadapi.modulos.comum.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ETipoPegada {

    PRONADA("Pronada"),
    SUPINADA("Supinada"),
    NEUTRA("Neutra"),
    CORDA("Corda");

    private final String descricao;
}
