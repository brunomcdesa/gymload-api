package br.com.gymloadapi.modulos.comum.enums;

import br.com.gymloadapi.modulos.registroatividade.factory.RegistroAtividadeFactory;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.service.RegistroCargaService;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ETipoExercicio {

    MUSCULACAO("Musculação", RegistroCargaService.class),
    CARDIO("Cardio", RegistroCargaService.class);

    private final String descricao;
    private final Class<? extends RegistroAtividadeFactory> serviceClass;
}
