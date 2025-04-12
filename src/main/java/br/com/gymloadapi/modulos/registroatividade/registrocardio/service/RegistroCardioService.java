package br.com.gymloadapi.modulos.registroatividade.registrocardio.service;

import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeRequest;
import br.com.gymloadapi.modulos.registroatividade.factory.RegistroAtividadeFactory;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistroCardioService implements RegistroAtividadeFactory {

    @Override
    public void salvarRegistro(RegistroAtividadeRequest request, Exercicio exercicio, Usuario usuario) {

    }
}
