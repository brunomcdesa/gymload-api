package br.com.gymloadapi.modulos.usuario.service;

import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.PermissaoException;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.usuario.dto.UsuarioRequest;
import br.com.gymloadapi.modulos.usuario.dto.UsuarioResponse;
import br.com.gymloadapi.modulos.usuario.mapper.UsuarioMapper;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import br.com.gymloadapi.modulos.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

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

    public void editar(UUID uuid, UsuarioRequest usuarioRequest, Usuario usuarioAutenticado) {
        var usuario = this.findByUuid(uuid);
        this.validarUsuarioAlteracao(usuario, usuarioAutenticado);

        usuarioMapper.editar(usuarioRequest, usuario);
        repository.save(usuario);
    }

    private void validarUsuarioAlteracao(Usuario usuarioAlterado, Usuario usuarioAutenticado) {
        if (!usuarioAutenticado.isAdmin() && !Objects.equals(usuarioAutenticado.getId(), usuarioAlterado.getId())) {
            throw new PermissaoException("Apenas usuários Admin ou o próprio usuário podem alterar suas informações.");
        }
    }

    private Usuario findByUuid(UUID uuid) {
        return repository.findByUuid(uuid).orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
    }
}
