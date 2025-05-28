package br.com.gymloadapi.modulos.usuario.service;

import br.com.gymloadapi.modulos.usuario.mapper.UsuarioMapper;
import br.com.gymloadapi.modulos.usuario.mapper.UsuarioMapperImpl;
import br.com.gymloadapi.modulos.usuario.model.UsuarioHistorico;
import br.com.gymloadapi.modulos.usuario.repository.UsuarioHistoricoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static br.com.gymloadapi.modulos.comum.enums.EAcao.CADASTRO;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuario;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UsuarioHistoricoServiceTest {

    private UsuarioHistoricoService service;
    private final UsuarioMapper usuarioMapper = new UsuarioMapperImpl();

    @Mock
    private UsuarioHistoricoRepository repository;
    @Captor
    private ArgumentCaptor<UsuarioHistorico> captor;

    @BeforeEach
    void setUp() {
        service = new UsuarioHistoricoService(usuarioMapper, repository);
    }

    @Test
    void salvar_deveSalvarHistorico_quandoSolicitado() {
        service.salvar(umUsuario(), null, CADASTRO);

        verify(repository).save(captor.capture());

        var historico = captor.getValue();
        assertAll(
            () -> assertEquals(2, historico.getUsuarioCadastroId()),
            () -> assertEquals(2, historico.getUsuario().getId()),
            () -> assertEquals(CADASTRO, historico.getAcao())
        );
    }
}
