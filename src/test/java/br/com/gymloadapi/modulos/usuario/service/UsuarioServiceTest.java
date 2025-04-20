package br.com.gymloadapi.modulos.usuario.service;

import br.com.gymloadapi.modulos.comum.exception.IntegracaoException;
import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.PermissaoException;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.comum.service.BackBlazeService;
import br.com.gymloadapi.modulos.usuario.mapper.UsuarioMapper;
import br.com.gymloadapi.modulos.usuario.mapper.UsuarioMapperImpl;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import br.com.gymloadapi.modulos.usuario.repository.UsuarioRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static br.com.gymloadapi.helper.TestsHelper.umMockMultipartFile;
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
    @Mock
    private BackBlazeService backBlazeService;
    @Captor
    private ArgumentCaptor<Usuario> captor;

    @BeforeEach
    void setUp() {
        service = new UsuarioService(mapper, repository, backBlazeService);
        ReflectionTestUtils.setField(service, "defaultUserImage", "defaultImage.jpeg");
    }

    @Test
    void cadastrar_deveLancarException_quandoJaExistirUsuarioComMesmoUsername() {
        when(repository.existsByUsername("usuario")).thenReturn(true);

        var exception = assertThrowsExactly(
            ValidacaoException.class,
            () -> service.cadastrar(umUsuarioRequest(), false, umMockMultipartFile())
        );
        assertEquals("Já existe um usuário com este username.", exception.getMessage());

        verify(repository).existsByUsername("usuario");
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(backBlazeService);
    }

    @Test
    void cadastrar_deveSalvarUsuarioComum_quandoForCadastroDeUsuarioComum() {
        var file = umMockMultipartFile();
        when(repository.existsByUsername("usuario")).thenReturn(false);

        service.cadastrar(umUsuarioRequest(), false, file);

        verify(repository).existsByUsername("usuario");
        verify(backBlazeService).uploadFile(anyString(), eq(file));
        verify(repository).save(captor.capture());

        var usuario = captor.getValue();
        assertAll(
            () -> assertEquals("Usuario", usuario.getNome()),
            () -> assertEquals("usuario", usuario.getUsername()),
            () -> assertFalse(usuario.getImagemPerfil().isBlank()),
            () -> assertTrue(usuario.getSenha().startsWith("$2a$")),
            () -> assertEquals(List.of(USER), usuario.getRoles())
        );
    }

    @Test
    void cadastrar_deveSalvarUsuarioAdmin_quandoForCadastroDeUsuarioAdmin() {
        when(repository.existsByUsername("usuarioAdmin")).thenReturn(false);

        service.cadastrar(umUsuarioAdminRequest(), true, null);

        verify(repository).existsByUsername("usuarioAdmin");
        verify(repository).save(captor.capture());
        verifyNoInteractions(backBlazeService);

        var usuario = captor.getValue();
        assertAll(
            () -> assertEquals("Usuario Admin", usuario.getNome()),
            () -> assertEquals("usuarioAdmin", usuario.getUsername()),
            () -> assertNull(usuario.getImagemPerfil()),
            () -> assertTrue(usuario.getSenha().startsWith("$2a$")),
            () -> assertEquals(List.of(ADMIN, USER), usuario.getRoles())
        );
    }

    @Test
    void findByUsername_deveRetornarUsuario_quandoEncontrarUsuarioComMesmoUsername() {
        when(repository.findByUsername("usuario")).thenReturn(Optional.of(umUsuarioAdmin()));

        var userDetails = service.findByUsername("usuario");
        assertAll(
            () -> assertEquals("usuarioAdmin", userDetails.getUsername()),
            () -> assertTrue(userDetails.getPassword().startsWith("$2a$"))
        );

        verify(repository).findByUsername("usuario");
    }

    @Test
    void findByUsername_deveLancarException_quandoNaoEncontrarUsuarioComMesmoUsername() {
        when(repository.findByUsername("usuario")).thenReturn(Optional.empty());

        var exception = assertThrowsExactly(
            NotFoundException.class,
            () -> service.findByUsername("usuario")
        );
        assertEquals("Usuário não encontrado.", exception.getMessage());

        verify(repository).findByUsername("usuario");
    }

    @Test
    void buscarTodos_deveRetornarTodosOsUsuarios_quandoSolicitado() {
        when(repository.findAll()).thenReturn(umaListaDeUsuarios());

        var response = service.buscarTodos();
        assertAll(
            () -> assertEquals("8689ea4e-3a85-4b6b-80f2-fc04f3cdd712", response.getFirst().uuid().toString()),
            () -> assertEquals("Usuario", response.getFirst().nome()),
            () -> assertEquals("usuarioUser", response.getFirst().username()),

            () -> assertEquals("c2d83d78-e1b2-4f7f-b79d-1b83f3c435f9", response.getLast().uuid().toString()),
            () -> assertEquals("Usuario Admin", response.getLast().nome()),
            () -> assertEquals("usuarioAdmin", response.getLast().username())
        );

        verify(repository).findAll();
    }

    @Test
    void editar_deveLancarException_quandoNaoEncontrarUsuario() {
        when(repository.findByUuid(USUARIO_ADMIN_UUID)).thenReturn(Optional.empty());

        var exception = assertThrowsExactly(
            NotFoundException.class,
            () -> service.editar(USUARIO_ADMIN_UUID, umUsuarioRequestSemSenha(), umMockMultipartFile(), umUsuarioAdmin())
        );
        assertEquals("Usuário não encontrado.", exception.getMessage());

        verify(repository).findByUuid(USUARIO_ADMIN_UUID);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(backBlazeService);
    }

    @Test
    void editar_deveLancarException_quandoUsuarioAutenticadoNaoForAdminNemOMesmoUsuarioQueEstaSendoEditado() {
        when(repository.findByUuid(USUARIO_ADMIN_UUID)).thenReturn(Optional.of(umUsuario()));

        var exception = assertThrowsExactly(
            PermissaoException.class,
            () -> service.editar(USUARIO_ADMIN_UUID, umUsuarioRequestSemSenha(), umMockMultipartFile(), outroUsuario())
        );
        assertEquals("Apenas usuários Admin ou o próprio usuário podem alterar suas informações.", exception.getMessage());

        verify(repository).findByUuid(USUARIO_ADMIN_UUID);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(backBlazeService);
    }

    @Test
    void editar_deveEditarUsuario_quandoUsuarioAutenticadoForAdmin() {
        when(repository.findByUuid(USUARIO_ADMIN_UUID)).thenReturn(Optional.of(umUsuario()));

        assertDoesNotThrow(() -> service.editar(USUARIO_ADMIN_UUID, umUsuarioRequestSemSenha(),
            umMockMultipartFile(), umUsuarioAdmin()));

        verify(repository).findByUuid(USUARIO_ADMIN_UUID);
        verify(repository).save(captor.capture());
        verify(backBlazeService).uploadFile(anyString(), any(MultipartFile.class));

        var usuario = captor.getValue();
        assertAll(
            () -> assertEquals("Usuario Edicao", usuario.getNome()),
            () -> assertEquals("usernameEdicao", usuario.getUsername()),
            () -> assertFalse(usuario.getImagemPerfil().isBlank())
        );
    }

    @Test
    void editar_deveEditarUsuario_quandoUsuarioAutenticadoForOMesmoUsuarioQueEstaSendoEditado() {
        when(repository.findByUuid(USUARIO_ADMIN_UUID)).thenReturn(Optional.of(umUsuario()));

        assertDoesNotThrow(() -> service.editar(USUARIO_ADMIN_UUID, umUsuarioRequestSemSenha(),
            umMockMultipartFile(), umUsuario()));

        verify(repository).findByUuid(USUARIO_ADMIN_UUID);
        verify(repository).save(captor.capture());
        verify(backBlazeService).uploadFile(anyString(), any(MultipartFile.class));

        var usuario = captor.getValue();
        assertAll(
            () -> assertEquals("Usuario Edicao", usuario.getNome()),
            () -> assertEquals("usernameEdicao", usuario.getUsername()),
            () -> assertFalse(usuario.getImagemPerfil().isBlank())
        );
    }

    @Test
    void editar_deveEditarUsuarioENaoDeveSalvarImagem_quandoNaoReceberImagem() {
        when(repository.findByUuid(USUARIO_ADMIN_UUID)).thenReturn(Optional.of(umUsuario()));

        assertDoesNotThrow(() -> service.editar(USUARIO_ADMIN_UUID, umUsuarioRequestSemSenha(), null, umUsuario()));

        verify(repository).findByUuid(USUARIO_ADMIN_UUID);
        verify(repository).save(captor.capture());
        verifyNoInteractions(backBlazeService);

        var usuario = captor.getValue();
        assertAll(
            () -> assertEquals("Usuario Edicao", usuario.getNome()),
            () -> assertEquals("usernameEdicao", usuario.getUsername()),
            () -> assertEquals("802421c7-f8fd-454e-ab59-9ea346a2a444-Usuario.png", usuario.getImagemPerfil())
        );
    }

    @Test
    void atualizarSenha_deveAtualizarSenha_quandoSolicitado() {
        service.atualizarSenha("usuarioUser", "123456");
        verify(repository).atualizarSenha("usuarioUser", "123456");
    }

    @Test
    @SneakyThrows
    void buscarImagemPerfil_deveBuscarImagemPerfil_quandoUsuarioPossuirImagemDePerfil() {
        var usuario = umUsuario();

        when(backBlazeService.downloadFile("usuarios-images/802421c7-f8fd-454e-ab59-9ea346a2a444-Usuario.png"))
            .thenReturn(umMockMultipartFile().getInputStream());

        assertNotNull(service.buscarImagemPerfil(usuario).getContentAsByteArray());

        verify(backBlazeService).downloadFile("usuarios-images/802421c7-f8fd-454e-ab59-9ea346a2a444-Usuario.png");
    }

    @SneakyThrows
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void buscarImagemPerfil_deveBuscarImagemPerfilDefault_quandoUsuarioNaoPossuirImagemDePerfil(String imagemPerfil) {
        var usuario = umUsuario();
        usuario.setImagemPerfil(imagemPerfil);

        when(backBlazeService.downloadFile("usuarios-images/defaultImage.jpeg"))
            .thenReturn(umMockMultipartFile().getInputStream());

        assertNotNull(service.buscarImagemPerfil(usuario).getContentAsByteArray());

        verify(backBlazeService).downloadFile("usuarios-images/defaultImage.jpeg");
    }

    @Test
    @SneakyThrows
    public void testBuscarImagemPerfil_QuandoOcorreIOException_DeveRetornarIntegracaoException() {
        var usuario = umUsuario();

        var mockInputStream = mock(InputStream.class);
        when(mockInputStream.readAllBytes()).thenThrow(new IOException("Erro na leitura do arquivo"));
        when(backBlazeService.downloadFile(anyString())).thenReturn(mockInputStream);

        var exception = assertThrowsExactly(
            IntegracaoException.class,
            () -> service.buscarImagemPerfil(usuario)
        );
        assertEquals("Erro ao buscar imagem de perfil.", exception.getMessage());

        verify(backBlazeService).downloadFile(anyString());
    }
}
