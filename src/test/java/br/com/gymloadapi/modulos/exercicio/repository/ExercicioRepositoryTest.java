package br.com.gymloadapi.modulos.exercicio.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static br.com.gymloadapi.modulos.usuario.helper.UsuarioHelper.umUsuarioAdmin;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
public class ExercicioRepositoryTest {

    @Autowired
    private ExercicioRepository repository;

    @Test
    void buscarExerciciosPorTreinoAndUsuario_deveRetornarExercicioPorTreino_quandoSolicitado() {
        var exercicios = repository.buscarExerciciosPorTreinoAndUsuario(1, umUsuarioAdmin().getId());
        assertAll(
            () -> assertEquals(1, exercicios.getFirst().getId()),
            () -> assertEquals("SUPINO RETO", exercicios.getFirst().getNome())
        );
    }
}
