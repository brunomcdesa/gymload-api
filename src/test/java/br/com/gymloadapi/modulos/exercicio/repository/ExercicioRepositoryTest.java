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
class ExercicioRepositoryTest {

    @Autowired
    private ExercicioRepository repository;

    @Test
    void findAllComplete_deveRetornarTodosOsExercicios_quandoSolicitado() {
        var exercicios = repository.findAllComplete();
        assertAll(
            () -> assertEquals(5, exercicios.size()),
            () -> assertEquals(1, exercicios.getFirst().getId()),
            () -> assertEquals("SUPINO RETO", exercicios.getFirst().getNome()),
            () -> assertEquals(2, exercicios.get(1).getId()),
            () -> assertEquals("ESTEIRA", exercicios.get(1).getNome()),
            () -> assertEquals(3, exercicios.get(2).getId()),
            () -> assertEquals("PUXADA ALTA", exercicios.get(2).getNome()),
            () -> assertEquals(4, exercicios.get(3).getId()),
            () -> assertEquals("ABDOMINAL", exercicios.get(3).getNome()),
            () -> assertEquals(5, exercicios.getLast().getId()),
            () -> assertEquals("PULL UP", exercicios.getLast().getNome())
        );
    }

    @Test
    void findAllCompleteByPredicate_deveRetornarTodosOsExercicios_quandoSolicitado() {
        var exercicios = repository.findAllCompleteByPredicate(umExercicioFiltroVazio().toPredicate());
        assertAll(
            () -> assertEquals(5, exercicios.size()),
            () -> assertEquals(1, exercicios.getFirst().getId()),
            () -> assertEquals("SUPINO RETO", exercicios.getFirst().getNome()),
            () -> assertEquals(2, exercicios.get(1).getId()),
            () -> assertEquals("ESTEIRA", exercicios.get(1).getNome()),
            () -> assertEquals(3, exercicios.get(2).getId()),
            () -> assertEquals("PUXADA ALTA", exercicios.get(2).getNome()),
            () -> assertEquals(4, exercicios.get(3).getId()),
            () -> assertEquals("ABDOMINAL", exercicios.get(3).getNome()),
            () -> assertEquals(5, exercicios.getLast().getId()),
            () -> assertEquals("PULL UP", exercicios.getLast().getNome())
        );
    }

    @Test
    void findAllCompleteByPredicate_deveRetornarTodosOsExerciciosFiltrados_quandoSolicitado() {
        var exercicios = repository.findAllCompleteByPredicate(umExercicioFiltro().toPredicate());
        assertAll(
            () -> assertEquals(5, exercicios.getFirst().getId()),
            () -> assertEquals("PULL UP", exercicios.getFirst().getNome())
        );
    }

    @Test
    void buscarExerciciosPorTreino_deveRetornarExercicioPorTreino_quandoSolicitado() {
        var exercicios = repository.buscarExerciciosPorTreino(1);
        assertAll(
            () -> assertEquals(1, exercicios.getFirst().getId()),
            () -> assertEquals("SUPINO RETO", exercicios.getFirst().getNome()),
            () -> assertEquals(2, exercicios.getLast().getId()),
            () -> assertEquals("ESTEIRA", exercicios.getLast().getNome())
        );
    }
}
