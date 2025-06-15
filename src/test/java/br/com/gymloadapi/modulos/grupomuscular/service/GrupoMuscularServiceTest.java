package br.com.gymloadapi.modulos.grupomuscular.service;

import br.com.gymloadapi.modulos.cache.config.CacheConfig;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Objects;
import java.util.Optional;

import static br.com.gymloadapi.modulos.cache.utils.CacheUtils.getCachesGruposMusculares;
import static br.com.gymloadapi.modulos.grupomuscular.helper.GrupoMuscularHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import({GrupoMuscularServiceTest.TestServiceConfig.class, CacheConfig.class})
class GrupoMuscularServiceTest {

    @TestConfiguration
    static class TestServiceConfig {
        @Bean
        public GrupoMuscularMapper grupoMuscularMapper() {
            return new GrupoMuscularMapperImpl();
        }

        @Bean
        public GrupoMuscularService grupoMuscularService(GrupoMuscularRepository repository,
                                                         GrupoMuscularMapper grupoMuscularMapper) {
            return new GrupoMuscularService(repository, grupoMuscularMapper);
        }
    }

    @Autowired
    private GrupoMuscularService service;
    @Autowired
    private CacheManager cacheManager;
    @MockitoBean
    private GrupoMuscularRepository repository;
    @Captor
    private ArgumentCaptor<GrupoMuscular> captor;

    @BeforeEach
    void setUp() {
        getCachesGruposMusculares().stream()
            .map(cacheManager::getCache)
            .filter(Objects::nonNull)
            .forEach(Cache::clear);
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
    void salvar_deveRemoverTodosOsCachesDeGruposMusculares_quandoSalvarUmNovoGrupoMuscular() {
        service.findAllResponse();
        service.findAllSelect();

        service.salvar(umGrupoMuscularRequest());

        service.findAllResponse();
        service.findAllSelect();

        verify(repository, times(4)).findAll();
        verify(repository).save(any(GrupoMuscular.class));
    }

    @Test
    void findAllResponse_deveRetornarGruposMusculares_quandoSolicitado() {
        when(repository.findAll()).thenReturn(umaListaGrupoMusculares());

        var responses = service.findAllResponse();

        assertAll(
            () -> assertEquals(1, responses.getFirst().id()),
            () -> assertEquals("Peitoral", responses.getFirst().nome()),
            () -> assertEquals(2, responses.get(1).id()),
            () -> assertEquals("Costas", responses.get(1).nome()),
            () -> assertEquals(3, responses.getLast().id()),
            () -> assertEquals("Abdomen", responses.getLast().nome())
        );

        verify(repository).findAll();
    }

    @Test
    void findAllResponse_deveRetornarDadosDoCache_quandoSolicitadoVariasVezes() {
        service.findAllResponse();
        service.findAllResponse();
        service.findAllResponse();

        verify(repository).findAll();
    }

    @Test
    void findAllSelect_deveRetornarSelectResponse_quandoSolicitado() {
        when(repository.findAll()).thenReturn(umaListaGrupoMusculares());

        var selectResponses = service.findAllSelect();

        assertAll(
            () -> assertEquals(1, selectResponses.getFirst().value()),
            () -> assertEquals("Peitoral", selectResponses.getFirst().label()),
            () -> assertEquals(2, selectResponses.get(1).value()),
            () -> assertEquals("Costas", selectResponses.get(1).label()),
            () -> assertEquals(3, selectResponses.getLast().value()),
            () -> assertEquals("Abdomen", selectResponses.getLast().label())
        );

        verify(repository).findAll();
    }

    @Test
    void findAllSelect_deveRetornarDadosDoCache_quandoSolicitadoVariasVezes() {
        service.findAllSelect();
        service.findAllSelect();
        service.findAllSelect();

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

    @Test
    void findById_deveRetornarDadosDoCache_quandoSolicitadoVariasVezes() {
        when(repository.findById(1)).thenReturn(Optional.of(umGrupoMuscularPeitoral()));

        service.findById(1);
        service.findById(1);
        service.findById(1);

        verify(repository).findById(1);
    }
}
