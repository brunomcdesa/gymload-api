package br.com.gymloadapi.modulos.comum.enums;

import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Aerobico;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Musculacao;
import br.com.gymloadapi.modulos.registroatividade.factory.RegistroAtividadeFactory;
import br.com.gymloadapi.modulos.registroatividade.registrocardio.service.RegistroCardioService;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.service.RegistroCargaService;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ETipoExercicio {

    MUSCULACAO("Musculação", Musculacao.class, RegistroCargaService.class),
    AEROBICO("Aeróbico", Aerobico.class, RegistroCardioService.class);

    private final String descricao;
    private final Class<?> groupValidator;
    private final Class<? extends RegistroAtividadeFactory> serviceClass;
}
