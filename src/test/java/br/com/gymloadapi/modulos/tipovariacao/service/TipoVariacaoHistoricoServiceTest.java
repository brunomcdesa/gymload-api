package br.com.gymloadapi.modulos.tipovariacao.service;

import br.com.gymloadapi.modulos.tipovariacao.mapper.TipoVariacaoMapper;
import br.com.gymloadapi.modulos.tipovariacao.mapper.TipoVariacaoMapperImpl;
import br.com.gymloadapi.modulos.tipovariacao.model.TipoVariacaoHistorico;
import br.com.gymloadapi.modulos.tipovariacao.repository.TipoVariacaoHistoricoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static br.com.gymloadapi.modulos.comum.enums.EAcao.CADASTRO;
import static br.com.gymloadapi.modulos.tipovariacao.helper.TipoVariacaoHelper.umTipoVariacao;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TipoVariacaoHistoricoServiceTest {

    private TipoVariacaoHistoricoService service;
    private final TipoVariacaoMapper mapper = new TipoVariacaoMapperImpl();

    @Mock
    private TipoVariacaoHistoricoRepository repository;
    @Captor
    private ArgumentCaptor<TipoVariacaoHistorico> historicoCaptor;

    @BeforeEach
    void setUp() {
        service = new TipoVariacaoHistoricoService(mapper, repository);
    }

    @Test
    void salvar_deveSalvarHistorico_quandoSolicitado() {
        service.salvar(umTipoVariacao(), 1, CADASTRO);

        verify(repository).save(historicoCaptor.capture());

        var historico = historicoCaptor.getValue();
        assertAll(
            () -> assertEquals(1, historico.getTipoVariacao().getId()),
            () -> assertEquals(CADASTRO, historico.getAcao()),
            () -> assertEquals(1, historico.getUsuarioCadastroId())
        );
    }
}
