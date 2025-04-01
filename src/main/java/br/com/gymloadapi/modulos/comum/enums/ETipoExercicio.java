package br.com.gymloadapi.modulos.comum.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ETipoExercicio {

    HALTER("Halter"),
    BARRA("Barra"),
    MAQUINA("Maquina"),
    POLIA("Polia"),
    ANILHA("Anilha"),
    BOLA("Bola"),
    KETLLEBEL("Kettlebel"),
    BAG("Bag"),
    CORPORAL("Corporal");

    private final String descricao;
}
