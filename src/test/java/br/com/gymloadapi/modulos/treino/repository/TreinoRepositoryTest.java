package br.com.gymloadapi.modulos.treino.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static br.com.gymloadapi.modulos.comum.enums.ESituacao.ATIVO;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TreinoRepositoryTest {

    @Autowired
    private TreinoRepository repository;

    @Test
    void findCompleteById_deveRetornarTreino_quandoEncontrarTreinoComEsteId() {
        var treino = repository.findCompleteById(1).get();

        assertAll(
            () -> assertEquals(1, treino.getId()),
            () -> assertEquals("TREINO 1", treino.getNome()),
            () -> assertEquals(LocalDate.of(2025, 3, 4), treino.getDataCadastro()),
            () -> assertEquals(ATIVO, treino.getSituacao()),
            () -> assertEquals(1, treino.getExercicios().getFirst().getId())
        );
    }

    @Test
    void findCompleteById_deveRetornarOptionalEmpty_quandoNaoEncontrarTreinoComEsteId() {
        assertTrue(repository.findCompleteById(9999999).isEmpty());
    }
}
