package br.com.gymloadapi.modulos.comum.enums;

import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Aerobico;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Musculacao;
import br.com.gymloadapi.modulos.registroatividade.factory.RegistroAtividadeFactory;
import br.com.gymloadapi.modulos.registroatividade.registroaerobico.service.RegistroAaerobicoService;
import br.com.gymloadapi.modulos.registroatividade.registromusculacao.service.RegistroMusculacaoService;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ETipoExercicio {

    MUSCULACAO("Musculação", Musculacao.class, RegistroMusculacaoService.class),
    AEROBICO("Aeróbico", Aerobico.class, RegistroAaerobicoService.class);

    private final String descricao;
    private final Class<?> groupValidator;
    private final Class<? extends RegistroAtividadeFactory> serviceClass;
}
