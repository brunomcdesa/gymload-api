package br.com.gymloadapi.modulos.comum.service;

import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.registroatividade.mapper.RegistroAtividadeMapper;
import br.com.gymloadapi.modulos.registroatividade.registrocardio.service.RegistroCardioService;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.mapper.HistoricoCargasMapper;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.repository.RegistroCargaRepository;
import br.com.gymloadapi.modulos.registroatividade.registrocarga.service.RegistroCargaService;
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
    RegistroCargaService.class,
    RegistroCardioService.class,
})
@MockitoBeans({
    @MockitoBean(types = ExercicioService.class),
    @MockitoBean(types = HistoricoCargasMapper.class),
    @MockitoBean(types = RegistroCargaRepository.class),
    @MockitoBean(types = RegistroAtividadeMapper.class)
})
class LocatorServiceTest {

    @Autowired
    private LocatorService service;

    @Test
    void getRegistroAtividadeService_deveRetornarRegistroCargaService_quandoSolicitadoComTipoExercicioMusculacao() {
        assertInstanceOf(RegistroCargaService.class, service.getRegistroAtividadeService(MUSCULACAO));
    }

    @Test
    void getRegistroAtividadeService_deveRetornarRegistroCardioService_quandoSolicitadoComTipoExercicioAerobico() {
        assertInstanceOf(RegistroCardioService.class, service.getRegistroAtividadeService(AEROBICO));
    }
}
