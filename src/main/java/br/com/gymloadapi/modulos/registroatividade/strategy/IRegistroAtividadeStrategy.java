package br.com.gymloadapi.modulos.registroatividade.strategy;

import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.registroatividade.dto.HistoricoRegistroAtividadeResponse;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeRequest;
import br.com.gymloadapi.modulos.registroatividade.dto.RegistroAtividadeResponse;
import br.com.gymloadapi.modulos.usuario.model.Usuario;

import java.util.List;

public interface IRegistroAtividadeStrategy {

    void salvarRegistro(RegistroAtividadeRequest request, Exercicio exercicio, Usuario usuario);

    RegistroAtividadeResponse buscarDestaque(Integer exercicioId, Integer usuarioId);

    List<HistoricoRegistroAtividadeResponse> buscarHistoricoRegistroCompleto(Integer exercicioId, Integer usuarioId);

    void editarRegistro(Integer registroAtividadeId, RegistroAtividadeRequest request, Usuario usuario);

    void excluirRegistro(Integer registroAtividadeId, Usuario usuario);

    void repetirUltimoRegistro(Exercicio exercicio, Usuario usuario);
}
