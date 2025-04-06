package br.com.gymloadapi.modulos.treino.service;

import br.com.gymloadapi.autenticacao.service.AutenticacaoService;
import br.com.gymloadapi.modulos.exercicio.service.ExercicioService;
import br.com.gymloadapi.modulos.treino.mapper.TreinoMapper;
import br.com.gymloadapi.modulos.treino.mapper.TreinoMapperImpl;
import br.com.gymloadapi.modulos.treino.model.Treino;
import br.com.gymloadapi.modulos.treino.repository.TreinoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umaListaDeExercicios;
import static br.com.gymloadapi.modulos.treino.helper.TreinoHelper.umTreino;
import static br.com.gymloadapi.modulos.treino.helper.TreinoHelper.umTreinoRequest;
import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TreinoServiceTest {

    private TreinoService service;
    private final TreinoMapper mapper = new TreinoMapperImpl();

    @Mock
    private TreinoRepository repository;
    @Mock
    private ExercicioService exercicioService;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Captor
    private ArgumentCaptor<Treino> captor;

    @BeforeEach
    void setUp() {
        service = new TreinoService(mapper, repository, exercicioService, autenticacaoService);
    }

    @Test
    void salvar_deveSalvarTreino_quandoSolicitado() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAdmin());
        when(exercicioService.findByIdIn(List.of(1, 2))).thenReturn(umaListaDeExercicios());

        service.salvar(umTreinoRequest());

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(exercicioService).findByIdIn(List.of(1, 2));
        verify(repository).save(captor.capture());

        var treino = captor.getValue();

        assertAll(
            () -> assertEquals("Um Treino", treino.getNome()),
            () -> assertEquals("Usuario Admin", treino.getUsuario().getNome()),
            () -> assertEquals("SUPINO RETO", treino.getExercicios().getFirst().getNome()),
            () -> assertEquals("PUXADA ALTA", treino.getExercicios().getLast().getNome())
        );
    }

    @Test
    void listarTodosDoUsuario_deveRetornarTreinosDoUsuario_quandoSolicitado() {
        var usuario = umUsuarioAdmin();
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuario);
        when(repository.findByUsuarioId(usuario.getId())).thenReturn(List.of(umTreino()));

        var response = service.listarTodosDoUsuario();
        assertAll(
            () -> assertEquals(1, response.getFirst().id()),
            () -> assertEquals("Um Treino", response.getFirst().nome()),
            () -> assertEquals(LocalDate.of(2025, 4, 6), response.getFirst().dataCadastro())
        );

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository).findByUsuarioId(usuario.getId());
    }
}
