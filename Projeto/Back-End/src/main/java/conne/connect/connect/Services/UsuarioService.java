package conne.connect.connect.Services;
import conne.connect.connect.Dto.UsuarioDTO;
import conne.connect.connect.Dto.UsuarioRequestDTO;
import conne.connect.connect.Models.UsuarioModel;
import conne.connect.connect.Repositories.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<UsuarioDTO> findAll(){
        return usuarioRepository.findAll().stream()
                .map(UsuarioDTO::fromModel)
                .toList();
    }

    public UsuarioDTO criarUsuario(UsuarioRequestDTO usuarioRequestDTO){
        validarEmailDisponivel(usuarioRequestDTO.getEmail(), null);
        UsuarioModel usuarioModel = usuarioRequestDTO.toModel();
        usuarioModel.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha()));
        return UsuarioDTO.fromModel(usuarioRepository.save(usuarioModel));
    }

    public UsuarioDTO buscarPorId(Long idUsuario){
        return UsuarioDTO.fromModel(buscarUsuarioExistente(idUsuario));
    }

    public UsuarioDTO atualizarUsuario(Long idUsuario, UsuarioRequestDTO usuarioRequestDTO){
        UsuarioModel usuario = buscarUsuarioExistente(idUsuario);
        validarEmailDisponivel(usuarioRequestDTO.getEmail(), idUsuario);
        usuarioRequestDTO.applyToModel(usuario);
        return UsuarioDTO.fromModel(usuarioRepository.save(usuario));
    }

    public void excluirUsuario(Long idUsuario){
        UsuarioModel usuario = buscarUsuarioExistente(idUsuario);
        usuarioRepository.delete(usuario);
    }

    private UsuarioModel buscarUsuarioExistente(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado."));
    }

    private void validarEmailDisponivel(String email, Long idUsuarioAtual) {
        boolean emailEmUso = idUsuarioAtual == null
                ? usuarioRepository.existsByEmail(email)
                : usuarioRepository.existsByEmailAndIdUsuarioNot(email, idUsuarioAtual);

        if (emailEmUso) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ja cadastrado.");
        }
    }
}
