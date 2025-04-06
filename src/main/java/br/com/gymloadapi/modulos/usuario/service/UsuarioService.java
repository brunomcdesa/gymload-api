package br.com.gymloadapi.modulos.usuario.service;

import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.usuario.dto.UsuarioRequest;
import br.com.gymloadapi.modulos.usuario.dto.UsuarioResponse;
import br.com.gymloadapi.modulos.usuario.mapper.UsuarioMapper;
import br.com.gymloadapi.modulos.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.gymloadapi.modulos.comum.utils.RolesUtils.ROLES_ADMIN;
import static br.com.gymloadapi.modulos.comum.utils.RolesUtils.ROLES_USER;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository repository;

    public void cadastrar(UsuarioRequest usuarioRequest, boolean isCadastroAdmin) {
        if (repository.existsByUsername(usuarioRequest.username())) {
            throw new ValidacaoException("Já existe um usuário com este username.");
        }

        var encryptedPassword = new BCryptPasswordEncoder().encode(usuarioRequest.password());
        var novoUsuario = usuarioMapper.mapToModel(usuarioRequest, encryptedPassword, isCadastroAdmin ? ROLES_ADMIN : ROLES_USER);

        repository.save(novoUsuario);
    }

    public UserDetails findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public List<UsuarioResponse> buscarTodos() {
        return repository.findAll().stream()
            .map(usuarioMapper::mapModelToResponse)
            .toList();
    }
}
