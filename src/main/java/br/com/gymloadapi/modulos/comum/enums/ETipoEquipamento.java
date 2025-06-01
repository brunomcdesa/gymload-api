package br.com.gymloadapi.modulos.comum.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ETipoEquipamento {

    HALTER("Halter"),
    BARRA("Barra"),
    MAQUINA("Maquina"),
    POLIA("Polia"),
    ANILHA("Anilha"),
    BOLA("Bola"),
    KETLLEBEL("Kettlebel"),
    BAG("Bag");

    private final String descricao;
}
