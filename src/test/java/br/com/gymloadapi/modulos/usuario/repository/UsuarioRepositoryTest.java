package br.com.gymloadapi.modulos.usuario.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static br.com.gymloadapi.modulos.comum.helper.ComumHelper.umEmail;
import static br.com.gymloadapi.modulos.comum.helper.ComumHelper.umEmailAdmin;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repository;

    @Test
    void existsByEmail_deveRetornarTrue_quandoExistirUsuarioComMesmoEmail() {
        assertTrue(repository.existsByEmail(umEmailAdmin()));
    }

    @Test
    void existsByEmail_deveRetornarFalse_quandoNaoExistirUsuarioComMesmoEmail() {
        assertFalse(repository.existsByEmail(umEmail()));
    }
}
