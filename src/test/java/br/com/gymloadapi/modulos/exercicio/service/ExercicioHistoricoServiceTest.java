package br.com.gymloadapi.modulos.exercicio.service;

import br.com.gymloadapi.modulos.exercicio.mapper.ExercicioMapper;
import br.com.gymloadapi.modulos.exercicio.mapper.ExercicioMapperImpl;
import br.com.gymloadapi.modulos.exercicio.model.ExercicioHistorico;
import br.com.gymloadapi.modulos.exercicio.repository.ExercicioHistoricoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static br.com.gymloadapi.modulos.comum.enums.EAcao.CADASTRO;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioMusculacao;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExercicioHistoricoServiceTest {

    private ExercicioHistoricoService service;
    private final ExercicioMapper mapper = new ExercicioMapperImpl();

    @Mock
    private ExercicioHistoricoRepository repository;
    @Captor
    private ArgumentCaptor<ExercicioHistorico> historicoCaptor;

    @BeforeEach
    void setUp() {
        service = new ExercicioHistoricoService(mapper, repository);
    }

    @Test
    void salvar_deveSalvarHistorico_quandoSolicitado() {
        service.salvar(umExercicioMusculacao(1), 1, CADASTRO);

        verify(repository).save(historicoCaptor.capture());

        var historico = historicoCaptor.getValue();
        assertAll(
            () -> assertEquals(CADASTRO, historico.getAcao()),
            () -> assertEquals(1, historico.getUsuarioCadastroId()),
            () -> assertEquals(1, historico.getExercicio().getId())
        );
    }
}
