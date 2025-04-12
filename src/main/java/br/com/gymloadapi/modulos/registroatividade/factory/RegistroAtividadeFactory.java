package br.com.gymloadapi.modulos.registroatividade.factory;

import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeRequest;
import br.com.gymloadapi.modulos.usuario.model.Usuario;

public interface RegistroAtividadeFactory {

    void salvarRegistro(RegistroAtividadeRequest request, Exercicio exercicio, Usuario usuario);
}
