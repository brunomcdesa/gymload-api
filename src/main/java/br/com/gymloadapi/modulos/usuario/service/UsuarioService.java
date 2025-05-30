package br.com.gymloadapi.modulos.usuario.service;

import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.comum.service.BackBlazeService;
import br.com.gymloadapi.modulos.usuario.dto.UsuarioRequest;
import br.com.gymloadapi.modulos.usuario.dto.UsuarioResponse;
import br.com.gymloadapi.modulos.usuario.mapper.UsuarioMapper;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import br.com.gymloadapi.modulos.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static br.com.gymloadapi.modulos.comum.utils.PasswordUtils.encodePassword;
import static br.com.gymloadapi.modulos.comum.utils.RolesUtils.ROLES_ADMIN;
import static br.com.gymloadapi.modulos.comum.utils.RolesUtils.ROLES_USER;
import static br.com.gymloadapi.modulos.comum.utils.ValidacaoUtils.validarUsuarioAlteracao;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final String MSG_USUARIO_NAO_ENCONTRADO = "Usuário não encontrado.";

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
        return repository.findByUsername(username)
            .orElseThrow(() -> new NotFoundException(MSG_USUARIO_NAO_ENCONTRADO));
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

    public UsuarioResponse buscarPorUuid(UUID uuid) {
        return usuarioMapper.mapModelToResponse(this.findByUuid(uuid));
    }

    public String buscarUrlImagemPerfil(Usuario usuario) {
        return backBlazeService.generatePresignedUrl(usuario.getImagemPerfil());
    }

    private Usuario findByUuid(UUID uuid) {
        return repository.findByUuid(uuid)
            .orElseThrow(() -> new NotFoundException(MSG_USUARIO_NAO_ENCONTRADO));
    }

    private void realizarUploadImagemPerfil(Usuario usuario, MultipartFile imagem) {
        var extensao = requireNonNull(imagem.getOriginalFilename())
            .substring(imagem.getOriginalFilename().lastIndexOf("."));
        var nomeComUnderLine = StringUtils.replace(usuario.getNome(), " ", "_");
        var imagemPerfilName = format("%s-%s%s", usuario.getUuid(), nomeComUnderLine, extensao);

        backBlazeService.uploadFile(imagemPerfilName, imagem);
        usuario.setImagemPerfil(imagemPerfilName);
    }
}
