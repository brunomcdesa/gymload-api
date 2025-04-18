package br.com.gymloadapi.modulos.usuario.service;

import br.com.gymloadapi.modulos.comum.exception.IntegracaoException;
import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.comum.service.BackBlazeService;
import br.com.gymloadapi.modulos.usuario.dto.UsuarioRequest;
import br.com.gymloadapi.modulos.usuario.dto.UsuarioResponse;
import br.com.gymloadapi.modulos.usuario.mapper.UsuarioMapper;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import br.com.gymloadapi.modulos.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static br.com.gymloadapi.modulos.comum.utils.PasswordUtils.encodePassword;
import static br.com.gymloadapi.modulos.comum.utils.RolesUtils.ROLES_ADMIN;
import static br.com.gymloadapi.modulos.comum.utils.RolesUtils.ROLES_USER;
import static br.com.gymloadapi.modulos.comum.utils.ValidacaoUtils.validarUsuarioAlteracao;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    @Value("${api.aws.default-user-image}")
    private String defaultUserImage;

    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository repository;
    private final BackBlazeService backBlazeService;

    public void cadastrar(UsuarioRequest usuarioRequest, boolean isCadastroAdmin, MultipartFile imagem) {
        if (repository.existsByUsername(usuarioRequest.username())) {
            throw new ValidacaoException("Já existe um usuário com este username.");
        }

        var novoUsuario = usuarioMapper.mapToModel(usuarioRequest, encodePassword(usuarioRequest.password()),
            isCadastroAdmin ? ROLES_ADMIN : ROLES_USER);

        if (imagem != null) {
            this.realizarUploadImagemPerfil(novoUsuario, imagem);
        }

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

    public void editar(UUID uuid, UsuarioRequest usuarioRequest, MultipartFile imagem, Usuario usuarioAutenticado) {
        var usuario = this.findByUuid(uuid);
        validarUsuarioAlteracao(usuario.getId(), usuarioAutenticado, "alterar suas informações");

        usuarioMapper.editar(usuarioRequest, usuario);
        if (imagem != null) {
            this.realizarUploadImagemPerfil(usuario, imagem);
        }
        repository.save(usuario);
    }

    public void atualizarSenha(String userName, String senha) {
        repository.atualizarSenha(userName, senha);
    }

    public Resource buscarImagemPerfil(Usuario usuario) {
        try {
            var nomeImagem = this.getNomeImagem(usuario);
            var imagemPerfil = backBlazeService.downloadFile(montarNomeArquivo(nomeImagem));
            return new ByteArrayResource(imagemPerfil.readAllBytes());
        } catch (IOException exception) {
            throw new IntegracaoException(exception, UsuarioService.class.getName(), "Erro ao buscar imagem de perfil.");
        }
    }

    private Usuario findByUuid(UUID uuid) {
        return repository.findByUuid(uuid).orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
    }

    private void realizarUploadImagemPerfil(Usuario usuario, MultipartFile imagem) {
        var extensao = requireNonNull(imagem.getOriginalFilename())
            .substring(imagem.getOriginalFilename().lastIndexOf("."));
        var imagemPerfilName = format("%s-%s%s", usuario.getUuid(), usuario.getNome(), extensao);

        backBlazeService.uploadFile(this.montarNomeArquivo(imagemPerfilName), imagem);
        usuario.setImagemPerfil(imagemPerfilName);
    }

    private String getNomeImagem(Usuario usuario) {
        return isNotBlank(usuario.getImagemPerfil())
            ? usuario.getImagemPerfil()
            : defaultUserImage;
    }

    private String montarNomeArquivo(String imagemPerfilName) {
        return format("usuarios-images/%s", imagemPerfilName);
    }
}
