package br.com.gymloadapi.modulos.usuario.service;

import br.com.gymloadapi.modulos.comum.enums.EAcao;
import br.com.gymloadapi.modulos.comum.exception.NotFoundException;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.comum.service.BackBlazeService;
import br.com.gymloadapi.modulos.comum.types.Email;
import br.com.gymloadapi.modulos.usuario.dto.UsuarioRequest;
import br.com.gymloadapi.modulos.usuario.dto.UsuarioResponse;
import br.com.gymloadapi.modulos.usuario.mapper.UsuarioMapper;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import br.com.gymloadapi.modulos.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static br.com.gymloadapi.modulos.comum.enums.EAcao.*;
import static br.com.gymloadapi.modulos.comum.utils.PasswordUtils.encodePassword;
import static br.com.gymloadapi.modulos.comum.utils.RolesUtils.ROLES_ADMIN;
import static br.com.gymloadapi.modulos.comum.utils.RolesUtils.ROLES_USER;
import static br.com.gymloadapi.modulos.comum.utils.ValidacaoUtils.validarUsuarioAlteracao;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final String MSG_USUARIO_NAO_ENCONTRADO = "Usuário não encontrado.";

    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository repository;
    private final BackBlazeService backBlazeService;
    private final UsuarioHistoricoService usuarioHistoricoService;

    public void cadastrar(UsuarioRequest usuarioRequest, boolean isCadastroAdmin, MultipartFile imagem,
                          Usuario usuarioAutenticado) {
        log.info("Iniciando cadastro de usuario: {}", usuarioRequest.username());
        var email = new Email(usuarioRequest.email());
        this.validarUsuarioExistente(usuarioRequest, email);
        log.info("Email valido e usuario inexistente no sistema.");

        var novoUsuario = usuarioMapper.mapToModel(usuarioRequest, encodePassword(usuarioRequest.password()),
            isCadastroAdmin ? ROLES_ADMIN : ROLES_USER, email);
        log.info("Novo usuario criado.");

        if (imagem != null) {
            this.realizarUploadImagemPerfil(novoUsuario, imagem);
            log.info("Imagem do usuario salva no backblaze.");
        }

        this.salvarComHistorico(novoUsuario, usuarioAutenticado, CADASTRO);
        log.info("Finalizando cadastro do usuario: {}", usuarioRequest.username());
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

        this.salvarComHistorico(usuario, usuarioAutenticado, EDICAO);
    }

    public void atualizarSenha(Usuario usuario, String senha) {
        usuario.alterarSenha(senha);
        this.salvarComHistorico(usuario, null, ALTERACAO_SENHA);
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

    private void validarUsuarioExistente(UsuarioRequest request, Email email) {
        this.validarUsuarioExistentePorUsername(request.username());
        this.validarUsuarioExistentePorEmail(email);

    }

    private void validarUsuarioExistentePorUsername(String username) {
        if (repository.existsByUsername(username)) {
            throw new ValidacaoException("Já existe um usuário com este username.");
        }
    }

    private void validarUsuarioExistentePorEmail(Email email) {
        if (repository.existsByEmail(email)) {
            throw new ValidacaoException("Já existe um usuário com este Email.");
        }
    }

    private void salvarComHistorico(Usuario usuario, Usuario usuarioAutenticado, EAcao acao) {
        repository.save(usuario);
        usuarioHistoricoService.salvar(usuario, usuarioAutenticado, acao);
    }
}
