package br.com.gymloadapi.modulos.gradesemanal.service;

import br.com.gymloadapi.modulos.gradesemanal.mapper.GradeSemanalMapper;
import br.com.gymloadapi.modulos.gradesemanal.mapper.GradeSemanalMapperImpl;
import br.com.gymloadapi.modulos.gradesemanal.model.GradeSemanalHistorico;
import br.com.gymloadapi.modulos.gradesemanal.repository.GradeSemanalHistoricoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static br.com.gymloadapi.modulos.comum.enums.EAcao.CADASTRO;
import static br.com.gymloadapi.modulos.comum.enums.EDiaSemana.SEGUNDA;
import static br.com.gymloadapi.modulos.gradesemanal.helper.GradeSemanalHelper.umaGradeSemanal;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GradeSemanalHistoricoServiceTest {

    private GradeSemanalHistoricoService service;
    private final GradeSemanalMapper mapper = new GradeSemanalMapperImpl();

    @Mock
    private GradeSemanalHistoricoRepository repository;
    @Captor
    private ArgumentCaptor<GradeSemanalHistorico> captor;

    @BeforeEach
    void setUp() {
        service = new GradeSemanalHistoricoService(mapper, repository);
    }

    @Test
    void salvar_deveSalvarHistorico_quandoSolicitado() {
        service.salvar(umaGradeSemanal(), 1, CADASTRO);

        verify(repository).save(captor.capture());

        var historico = captor.getValue();
        assertAll(
            () -> assertNull(historico.getId()),
            () -> assertEquals(CADASTRO, historico.getAcao()),
            () -> assertEquals(1, historico.getUsuarioCadastroId()),
            () -> assertEquals(SEGUNDA, historico.getDiaSemana()),
            () -> assertEquals(1, historico.getTreino().getId()),
            () -> assertEquals(1, historico.getUsuario().getId())
        );
    }
}
