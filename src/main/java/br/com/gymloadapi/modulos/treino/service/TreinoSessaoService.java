package br.com.gymloadapi.modulos.treino.service;

import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.registroatividade.service.RegistroAtividadeService;
import br.com.gymloadapi.modulos.treino.model.TreinoSessao;
import br.com.gymloadapi.modulos.treino.repository.TreinoSessaoRepository;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static br.com.gymloadapi.modulos.comum.enums.EAcao.CADASTRO;

@Service
@RequiredArgsConstructor
public class TreinoSessaoService {

    private final TreinoSessaoRepository repository;
    private final TreinoSessaoHistoricoService historicoService;
    private final TreinoService treinoService;
    private final RegistroAtividadeService registroAtividadeService;

    public void finalizar(Integer treinoId, Usuario usuario) {
        var treino = treinoService.findByIdAndUsuarioId(treinoId, usuario.getId());

        if (!registroAtividadeService.existeRegistroHojeParaExercicios(treino.getExerciciosIds(), usuario.getId())) {
            throw new ValidacaoException("Registre pelo menos um exercício antes de finalizar o treino.");
        }

        var sessao = repository.save(TreinoSessao.builder()
            .dataSessao(LocalDate.now())
            .treino(treino)
            .usuario(usuario)
            .build());

        historicoService.salvar(sessao, usuario.getId(), CADASTRO);
    }
}
