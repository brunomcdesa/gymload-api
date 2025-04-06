package br.com.gymloadapi.modulos.usuario.service;

import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.usuario.mapper.UsuarioMapper;
import br.com.gymloadapi.modulos.usuario.mapper.UsuarioMapperImpl;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import br.com.gymloadapi.modulos.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static br.com.gymloadapi.modulos.usuario.enums.EUserRole.ADMIN;
import static br.com.gymloadapi.modulos.usuario.enums.EUserRole.USER;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    private UsuarioService service;
    private final UsuarioMapper mapper = new UsuarioMapperImpl();

    @Mock
    private UsuarioRepository repository;
    @Captor
    private ArgumentCaptor<Usuario> captor;

    @BeforeEach
    void setUp() {
        service = new UsuarioService(mapper, repository);
    }

    @Test
    void cadastrar_deveLancarException_quandoJaExistirUsuarioComMesmoUsername() {
        when(repository.existsByUsername("usuario")).thenReturn(true);

        var exception = assertThrowsExactly(
            ValidacaoException.class,
            () -> service.cadastrar(umUsuarioRequest(), false)
        );
        assertEquals("Já existe um usuário com este username.", exception.getMessage());

        verify(repository).existsByUsername("usuario");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void cadastrar_deveSalvarUsuarioComum_quandoForCadastroDeUsuarioComum() {
        when(repository.existsByUsername("usuario")).thenReturn(false);

        service.cadastrar(umUsuarioRequest(), false);

        verify(repository).existsByUsername("usuario");
        verify(repository).save(captor.capture());

        var usuario = captor.getValue();
        assertAll(
            () -> assertEquals("Usuario", usuario.getNome()),
            () -> assertEquals("usuario", usuario.getUsername()),
            () -> assertTrue(usuario.getSenha().startsWith("$2a$")),
            () -> assertEquals(List.of(USER), usuario.getRoles())
        );
    }

    @Test
    void cadastrar_deveSalvarUsuarioAdmin_quandoForCadastroDeUsuarioAdmin() {
        when(repository.existsByUsername("usuarioAdmin")).thenReturn(false);

        service.cadastrar(umUsuarioAdminRequest(), true);

        verify(repository).existsByUsername("usuarioAdmin");
        verify(repository).save(captor.capture());

        var usuario = captor.getValue();
        assertAll(
            () -> assertEquals("Usuario Admin", usuario.getNome()),
            () -> assertEquals("usuarioAdmin", usuario.getUsername()),
            () -> assertTrue(usuario.getSenha().startsWith("$2a$")),
            () -> assertEquals(List.of(ADMIN, USER), usuario.getRoles())
        );
    }

    @Test
    void findByUsername_deveRetornarUsuario_quandoEncontrarUsuarioComMesmoUsername() {
        when(repository.findByUsername("usuario")).thenReturn(umUsuarioAdmin());

        var userDetails = service.findByUsername("usuario");
        assertAll(
            () -> assertEquals("usuarioAdmin", userDetails.getUsername()),
            () -> assertTrue(userDetails.getPassword().startsWith("$2a$"))
        );

        verify(repository).findByUsername("usuario");
    }

    @Test
    void buscarTodos_deveRetornarTodosOsUsuarios_quandoSolicitado() {
        when(repository.findAll()).thenReturn(umaListaDeUsuarios());

        var response = service.buscarTodos();
        assertAll(
            () -> assertEquals("8689ea4e-3a85-4b6b-80f2-fc04f3cdd712", response.getFirst().id().toString()),
            () -> assertEquals("Usuario", response.getFirst().nome()),
            () -> assertEquals("usuarioUser", response.getFirst().username()),

            () -> assertEquals("c2d83d78-e1b2-4f7f-b79d-1b83f3c435f9", response.getLast().id().toString()),
            () -> assertEquals("Usuario Admin", response.getLast().nome()),
            () -> assertEquals("usuarioAdmin", response.getLast().username())
        );

        verify(repository).findAll();
    }
}
