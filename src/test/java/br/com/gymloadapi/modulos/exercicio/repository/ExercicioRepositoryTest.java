package br.com.gymloadapi.modulos.exercicio.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioFiltro;
import static br.com.gymloadapi.modulos.exercicio.helper.ExercicioHelper.umExercicioFiltroVazio;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
public class ExercicioRepositoryTest {

    @Autowired
    private ExercicioRepository repository;

    @Test
    void findAllComplete_deveRetornarTodosOsExercicios_quandoSolicitado() {
        var exercicios = repository.findAllComplete();
        assertAll(
            () -> assertEquals(2, exercicios.size()),
            () -> assertEquals(1, exercicios.getFirst().getId()),
            () -> assertEquals("SUPINO RETO", exercicios.getFirst().getNome()),
            () -> assertEquals(2, exercicios.getLast().getId()),
            () -> assertEquals("PUXADA ALTA", exercicios.getLast().getNome())
        );
    }

    @Test
    void findAllCompleteByPredicate_deveRetornarTodosOsExercicios_quandoSolicitado() {
        var exercicios = repository.findAllCompleteByPredicate(umExercicioFiltroVazio().toPredicate());
        assertAll(
            () -> assertEquals(2, exercicios.size()),
            () -> assertEquals(1, exercicios.getFirst().getId()),
            () -> assertEquals("SUPINO RETO", exercicios.getFirst().getNome()),
            () -> assertEquals(2, exercicios.getLast().getId()),
            () -> assertEquals("PUXADA ALTA", exercicios.getLast().getNome())
        );
    }

    @Test
    void findAllCompleteByPredicate_deveRetornarTodosOsExerciciosFiltrados_quandoSolicitado() {
        var exercicios = repository.findAllCompleteByPredicate(umExercicioFiltro().toPredicate());
        assertAll(
            () -> assertEquals(2, exercicios.getFirst().getId()),
            () -> assertEquals("PUXADA ALTA", exercicios.getFirst().getNome())
        );
    }

    @Test
    void buscarExerciciosPorTreino_deveRetornarExercicioPorTreino_quandoSolicitado() {
        var exercicios = repository.buscarExerciciosPorTreino(1);
        assertAll(
            () -> assertEquals(1, exercicios.getFirst().getId()),
            () -> assertEquals("SUPINO RETO", exercicios.getFirst().getNome())
        );
    }
}
