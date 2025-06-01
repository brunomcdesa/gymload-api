package br.com.gymloadapi.modulos.comum.enums;

import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Aerobico;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Calistenia;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Musculacao;
import br.com.gymloadapi.modulos.registroatividade.strategy.IRegistroAtividadeStrategy;
import br.com.gymloadapi.modulos.registroatividade.registroaerobico.service.RegistroAerobicoService;
import br.com.gymloadapi.modulos.registroatividade.registrocalistenia.service.RegistroCalisteniaService;
import br.com.gymloadapi.modulos.registroatividade.registromusculacao.service.RegistroMusculacaoService;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum ETipoExercicio {

    MUSCULACAO("Musculação", Musculacao.class, RegistroMusculacaoService.class),
    AEROBICO("Aeróbico", Aerobico.class, RegistroAerobicoService.class),
    CALISTENIA("Calistenia", Calistenia.class, RegistroCalisteniaService.class),;

    private final String descricao;
    private final Class<?> groupValidator;
    private final Class<? extends IRegistroAtividadeStrategy> serviceClass;

    public static List<ETipoExercicio> getTiposExerciciosQuePossuemGrupoMuscular() {
        return List.of(MUSCULACAO, CALISTENIA);
    }
}
