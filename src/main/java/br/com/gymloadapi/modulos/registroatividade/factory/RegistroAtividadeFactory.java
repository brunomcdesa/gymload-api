package br.com.gymloadapi.modulos.registroatividade.factory;

import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.registroatividade.dto.HistoricoRegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeRequest;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeResponse;
import br.com.gymloadapi.modulos.usuario.model.Usuario;

import java.util.List;

public interface RegistroAtividadeFactory {

    void salvarRegistro(RegistroAtividadeRequest request, Exercicio exercicio, Usuario usuario);

    RegistroAtividadeResponse buscarUltimoRegistro(Integer exercicioId, Integer usuarioId);

    List<HistoricoRegistroAtividadeResponse> buscarHistoricoRegistroCompleto(Integer exercicioId, Integer usuarioId);

    void editarRegistro(Integer registroAtividadeId, RegistroAtividadeRequest request, Usuario usuario);
}
