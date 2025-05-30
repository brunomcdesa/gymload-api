package br.com.gymloadapi.modulos.grupomuscular.service;

import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.grupomuscular.mapper.GrupoMuscularMapper;
import br.com.gymloadapi.modulos.grupomuscular.mapper.GrupoMuscularMapperImpl;
import br.com.gymloadapi.modulos.grupomuscular.model.GrupoMuscular;
import br.com.gymloadapi.modulos.grupomuscular.repository.GrupoMuscularRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static br.com.gymloadapi.modulos.grupomuscular.helper.GrupoMuscularHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrupoMuscularServiceTest {

    private GrupoMuscularService service;
    private final GrupoMuscularMapper mapper = new GrupoMuscularMapperImpl();

    @Mock
    private GrupoMuscularRepository repository;
    @Captor
    private ArgumentCaptor<GrupoMuscular> captor;

    @BeforeEach
    void setUp() {
        service = new GrupoMuscularService(repository, mapper);
    }

    @Test
    void salvar_deveSalvarGrupoMuscular_quandoSolicitado() {
        service.salvar(umGrupoMuscularRequest());

        verify(repository).save(captor.capture());

        var grupoMuscular = captor.getValue();
        assertAll(
            () -> assertEquals("Peitoral", grupoMuscular.getNome()),
            () -> assertEquals("PEITORAL", grupoMuscular.getCodigo())
        );
    }

    @Test
    void findAllResponse_deveRetornarGruposMusculares_quandoSolicitado() {
        when(repository.findAll()).thenReturn(umaListaGrupoMusculares());

        var responses = service.findAllResponse();

        assertAll(
            () -> assertEquals(1, responses.getFirst().id()),
            () -> assertEquals("Peitoral", responses.getFirst().nome()),
            () -> assertEquals(2, responses.getLast().id()),
            () -> assertEquals("Costas", responses.getLast().nome())
        );

        verify(repository).findAll();
    }

    @Test
    void findAllSelect_deveRetornarSelectResponse_quandoSolicitado() {
        when(repository.findAll()).thenReturn(umaListaGrupoMusculares());

        var selectResponses = service.findAllSelect();

        assertAll(
            () -> assertEquals(1, selectResponses.getFirst().value()),
            () -> assertEquals("Peitoral", selectResponses.getFirst().label()),
            () -> assertEquals(2, selectResponses.getLast().value()),
            () -> assertEquals("Costas", selectResponses.getLast().label())
        );

        verify(repository).findAll();
    }

    @Test
    void findById_deveLancarException_quandoNaoEncontrarGrupoMuscular() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        var exception = assertThrowsExactly(NotFoundException.class, () -> service.findById(1));
        assertEquals("Grupo Muscular não encontrado.", exception.getMessage());

        verify(repository).findById(1);
    }

    @Test
    void findById_deveRetornarGrupoMuscular_quandoEncontrarGrupoMuscular() {
        when(repository.findById(1)).thenReturn(Optional.of(umGrupoMuscularPeitoral()));

        var grupoMuscular = assertDoesNotThrow(() -> service.findById(1));
        assertAll(
            () -> assertEquals(1, grupoMuscular.getId()),
            () -> assertEquals("Peitoral", grupoMuscular.getNome()),
            () -> assertEquals("PEITORAL", grupoMuscular.getCodigo())
        );

        verify(repository).findById(1);
    }
}
