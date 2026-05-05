package conne.connect.connect.Services;

import conne.connect.connect.Dto.UsuarioDTO;
import conne.connect.connect.Dto.UsuarioRequestDTO;
import conne.connect.connect.Models.UsuarioModel;
import conne.connect.connect.Repositories.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioDTO> findAll() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioDTO::fromModel)
                .toList();
    }

    public UsuarioDTO criarUsuario(UsuarioRequestDTO usuarioRequestDTO) {
        validarEmailDisponivel(usuarioRequestDTO.getEmail(), null);
        UsuarioModel usuarioModel = usuarioRequestDTO.toModel();
        // transforma a senha normal em hash
        usuarioModel.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha()));
        return UsuarioDTO.fromModel(usuarioRepository.save(usuarioModel));
    }

    public UsuarioDTO buscarPorId(Long idUsuario) {
        return UsuarioDTO.fromModel(buscarUsuarioExistente(idUsuario));
    }

    public UsuarioDTO atualizarUsuario(Long idUsuario, UsuarioRequestDTO usuarioRequestDTO) {
        UsuarioModel usuario = buscarUsuarioExistente(idUsuario);
        validarEmailDisponivel(usuarioRequestDTO.getEmail(), idUsuario);
        usuarioRequestDTO.applyToModel(usuario);
        usuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha()));
        return UsuarioDTO.fromModel(usuarioRepository.save(usuario));
    }

    public void excluirUsuario(Long idUsuario) {
        UsuarioModel usuario = buscarUsuarioExistente(idUsuario);
        usuarioRepository.delete(usuario);
    }

    private UsuarioModel buscarUsuarioExistente(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
    }

    private void validarEmailDisponivel(String email, Long idUsuarioAtual) {
        boolean emailJaExiste;
        if (idUsuarioAtual == null) {
            emailJaExiste = usuarioRepository.existsByEmail(email);
        } else {
            emailJaExiste = usuarioRepository.existsByEmailAndIdUsuarioNot(email, idUsuarioAtual);
        }
        if (emailJaExiste) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já está em uso.");
        }
    }
}