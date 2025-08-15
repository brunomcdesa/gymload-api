package br.com.gymloadapi.modulos.exercicio.service;

import br.com.gymloadapi.modulos.exercicio.mapper.ExercicioMapper;
import br.com.gymloadapi.modulos.exercicio.mapper.ExercicioMapperImpl;
import br.com.gymloadapi.modulos.exercicio.model.ExercicioVariacaoHistorico;
import br.com.gymloadapi.modulos.exercicio.repository.ExercicioVariacaoHistoricoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static br.com.gymloadapi.modulos.comum.enums.EAcao.CADASTRO;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioVariacao;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExercicioVariacaoHistoricoServiceTest {

    private ExercicioVariacaoHistoricoService service;
    private final ExercicioMapper mapper = new ExercicioMapperImpl();

    @Mock
    private ExercicioVariacaoHistoricoRepository repository;
    @Captor
    private ArgumentCaptor<ExercicioVariacaoHistorico> historicoCaptor;

    @BeforeEach
    void setUp() {
        service = new ExercicioVariacaoHistoricoService(mapper, repository);
    }

    @Test
    void salvar_deveSalvarHistorico_quandoSolicitado() {
        service.salvar(umExercicioVariacao(), 1, CADASTRO);

        verify(repository).save(historicoCaptor.capture());

        var historico = historicoCaptor.getValue();
        assertAll(
            () -> assertEquals(CADASTRO, historico.getAcao()),
            () -> assertEquals(1, historico.getUsuarioCadastroId()),
            () -> assertEquals(1, historico.getExercicioVariacao().getId())
        );
    }
}
