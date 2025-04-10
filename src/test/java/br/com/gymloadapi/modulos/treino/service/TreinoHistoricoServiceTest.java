package br.com.gymloadapi.modulos.treino.service;

import br.com.gymloadapi.modulos.treino.mapper.TreinoMapper;
import br.com.gymloadapi.modulos.treino.mapper.TreinoMapperImpl;
import br.com.gymloadapi.modulos.treino.model.TreinoHistorico;
import br.com.gymloadapi.modulos.treino.repository.TreinoHistoricoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static br.com.gymloadapi.modulos.comum.enums.EAcao.INATIVACAO;
import static br.com.gymloadapi.modulos.comum.enums.ESituacao.INATIVO;
import static br.com.gymloadapi.modulos.treino.helper.TreinoHelper.umTreino;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TreinoHistoricoServiceTest {

    private TreinoHistoricoService service;
    private TreinoMapper mapper = new TreinoMapperImpl();

    @Mock
    private TreinoHistoricoRepository repository;
    @Captor
    private ArgumentCaptor<TreinoHistorico> captor;

    @BeforeEach
    void setUp() {
        service = new TreinoHistoricoService(mapper, repository);
    }

    @Test
    void salvar_deveSalvarHistorico_quandoSolicitado() {
        service.salvar(umTreino(INATIVO), 1, INATIVACAO);

        verify(repository).save(captor.capture());

        var historico = captor.getValue();
        assertAll(
            () -> assertNull(historico.getId()),
            () -> assertEquals(INATIVACAO, historico.getAcao()),
            () -> assertEquals(1, historico.getUsuarioCadastroId()),
            () -> assertEquals(1, historico.getTreino().getId())
        );
    }
}
