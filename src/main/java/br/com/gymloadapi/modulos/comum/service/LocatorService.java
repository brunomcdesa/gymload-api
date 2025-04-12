package br.com.gymloadapi.modulos.comum.service;

import br.com.gymloadapi.modulos.comum.enums.ETipoExercicio;
import br.com.gymloadapi.modulos.registroatividade.factory.RegistroAtividadeFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocatorService {

    private final ApplicationContext applicationContext;

    public RegistroAtividadeFactory getRegistroAtividadeService(ETipoExercicio tipoExercicio) {
        return applicationContext.getBean(tipoExercicio.getServiceClass());
    }
}
