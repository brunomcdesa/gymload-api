package br.com.gymloadapi.modulos.comum.service;

import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.registroatividade.mapper.RegistroAtividadeMapper;
import br.com.gymloadapi.modulos.registroatividade.registroaerobico.repository.RegistroAerobicoRepository;
import br.com.gymloadapi.modulos.registroatividade.registroaerobico.service.RegistroAaerobicoService;
import br.com.gymloadapi.modulos.registroatividade.registromusculacao.repository.RegistroMusculacaoRepository;
import br.com.gymloadapi.modulos.registroatividade.registromusculacao.service.RegistroMusculacaoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoBeans;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.AEROBICO;
import static br.com.gymloadapi.modulos.comum.enums.ETipoExercicio.MUSCULACAO;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    LocatorService.class,
    RegistroMusculacaoService.class,
    RegistroAaerobicoService.class,
})
@MockitoBeans({
    @MockitoBean(types = ExercicioService.class),
    @MockitoBean(types = RegistroMusculacaoRepository.class),
    @MockitoBean(types = RegistroAtividadeMapper.class),
    @MockitoBean(types = RegistroAerobicoRepository.class),
})
class LocatorServiceTest {

    @Autowired
    private LocatorService service;

    @Test
    void getRegistroAtividadeService_deveRetornarRegistroMusculacaoService_quandoSolicitadoComTipoExercicioMusculacao() {
        assertInstanceOf(RegistroMusculacaoService.class, service.getRegistroAtividadeService(MUSCULACAO));
    }

    @Test
    void getRegistroAtividadeService_deveRetornarRegistroAerobicoService_quandoSolicitadoComTipoExercicioAerobico() {
        assertInstanceOf(RegistroAaerobicoService.class, service.getRegistroAtividadeService(AEROBICO));
    }
}
