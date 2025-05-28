package br.com.gymloadapi.modulos.usuario.service;

import br.com.gymloadapi.modulos.comum.enums.EAcao;
import br.com.gymloadapi.modulos.usuario.mapper.UsuarioMapper;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import br.com.gymloadapi.modulos.usuario.repository.UsuarioHistoricoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static br.com.gymloadapi.modulos.comum.utils.MapUtils.mapNullWithBackup;

@Service
@RequiredArgsConstructor
public class UsuarioHistoricoService {

    private final UsuarioMapper usuarioMapper;
    private final UsuarioHistoricoRepository repository;

    public void salvar(Usuario usuario, Usuario usuarioAutenticado, EAcao acao) {
        var historico = usuarioMapper.mapToHistorico(usuario,
            mapNullWithBackup(usuarioAutenticado, Usuario::getId, usuario.getId()),
            acao);

        repository.save(historico);
    }
}
