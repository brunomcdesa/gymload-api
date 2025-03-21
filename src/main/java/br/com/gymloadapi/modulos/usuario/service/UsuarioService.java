package br.com.gymloadapi.modulos.usuario.service;

import br.com.gymloadapi.autenticacao.dto.CadastroRequest;
import br.com.gymloadapi.modulos.usuario.dto.UsuarioResponse;
import br.com.gymloadapi.modulos.usuario.mapper.UsuarioMapper;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import br.com.gymloadapi.modulos.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.validation.ValidationException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioMapper mapper;
    private final UsuarioRepository repository;

    public void cadastrar(CadastroRequest cadastroRequest) {
        if (repository.existsByUsername(cadastroRequest.username())) {
            throw new ValidationException("Usuário já existente");
        }

        var encryptedPassword = new BCryptPasswordEncoder().encode(cadastroRequest.password());
        var novoUsuario = Usuario.createUser(cadastroRequest, encryptedPassword);

        repository.save(novoUsuario);
    }

    public UserDetails findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public List<UsuarioResponse> buscarTodos() {
        return repository.findAll().stream()
            .map(mapper::mapModelToResponse)
            .toList();
    }
}
