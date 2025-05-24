package br.com.gymloadapi.modulos.usuario.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;

import static br.com.gymloadapi.modulos.comum.helper.ComumHelper.umEmail;
import static br.com.gymloadapi.modulos.comum.helper.ComumHelper.umEmailAdmin;
import static br.com.gymloadapi.modulos.comum.utils.PasswordUtils.encodePassword;
import static br.com.gymloadapi.modulos.comum.utils.PasswordUtils.isPasswordEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repository;
    @Autowired
    private EntityManager entityManager;

    @Test
    void atualizarSenha_deveAtualizarSenha_quandoSolicitado() {
        var senhaAntiga = repository.findById(1).get().getSenha();
        assertTrue(isPasswordEquals("123456", senhaAntiga));

        repository.atualizarSenha("USUARIO_ADMIN", encodePassword("654321"));

        entityManager.flush();
        entityManager.clear();

        var senhaNova = repository.findById(1).get().getSenha();
        assertTrue(isPasswordEquals("654321", senhaNova));
    }

    @Test
    void existsByEmail_deveRetornarTrue_quandoExistirUsuarioComMesmoEmail() {
        assertTrue(repository.existsByEmail(umEmailAdmin()));
    }

    @Test
    void existsByEmail_deveRetornarFalse_quandoNaoExistirUsuarioComMesmoEmail() {
        assertFalse(repository.existsByEmail(umEmail()));
    }
}
