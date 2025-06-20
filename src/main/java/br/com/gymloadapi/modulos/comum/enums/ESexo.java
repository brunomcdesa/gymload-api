package br.com.gymloadapi.modulos.comum.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ESexo {

    MASCULINO("Masculino"),
    FEMININO("Feminino");

    private final String descricao;
}
