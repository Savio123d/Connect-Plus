package conne.connect.connect.Usuario.service;

import conne.connect.connect.Usuario.dto.UsuarioDTO;
import conne.connect.connect.Usuario.dto.UsuarioRequestDTO;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.model.UsuarioModel;
import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;
import conne.connect.connect.Usuario.repository.UsuarioRepository;
import conne.connect.connect.Xp.model.SaldoXpModel;
import conne.connect.connect.Xp.repository.SaldoXpRepository;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final SaldoXpRepository saldoXpRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            UsuarioEmpresaRepository usuarioEmpresaRepository,
            SaldoXpRepository saldoXpRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioEmpresaRepository = usuarioEmpresaRepository;
        this.saldoXpRepository = saldoXpRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioDTO::fromModel)
                .toList();
    }

    @Cacheable(value = "usuariosPorEmpresa", key = "#idEmpresa")
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarUsuariosDaEmpresa(Long idEmpresa) {
        return usuarioEmpresaRepository.findByIdEmpresa_IdEmpresa(idEmpresa)
                .stream()
                .map(this::montarUsuarioEmpresaDTO)
                .toList();
    }

    public UsuarioDTO criarUsuario(UsuarioRequestDTO usuarioRequestDTO) {
        validarEmailDisponivel(usuarioRequestDTO.getEmail(), null);

        UsuarioModel usuarioModel = usuarioRequestDTO.toModel();

        usuarioModel.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha()));

        return UsuarioDTO.fromModel(usuarioRepository.save(usuarioModel));
    }

    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorId(Long idUsuario){
        return UsuarioDTO.fromModel(buscarUsuarioExistente(idUsuario));
    }

    public UsuarioDTO atualizarUsuario(Long idUsuario, UsuarioRequestDTO usuarioRequestDTO){
        UsuarioModel usuario = buscarUsuarioExistente(idUsuario);

        validarEmailDisponivel(usuarioRequestDTO.getEmail(), idUsuario);

        usuarioRequestDTO.applyToModel(usuario);

        usuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha()));

        return UsuarioDTO.fromModel(usuarioRepository.save(usuario));
    }

    public void excluirUsuario(Long idUsuario){
        UsuarioModel usuario = buscarUsuarioExistente(idUsuario);
        usuarioRepository.delete(usuario);
    }

    private UsuarioDTO montarUsuarioEmpresaDTO(UsuarioEmpresaModel usuarioEmpresa) {
        SaldoXpModel saldoXp = saldoXpRepository
                .findByIdUsuarioEmpresa_IdUsuarioEmpresa(usuarioEmpresa.getIdUsuarioEmpresa())
                .orElse(null);

        return UsuarioDTO.fromUsuarioEmpresa(usuarioEmpresa, saldoXp);
    }

    private UsuarioModel buscarUsuarioExistente(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario nao encontrado."
                ));
    }

    private void validarEmailDisponivel(String email, Long idUsuarioAtual) {
        boolean emailEmUso = idUsuarioAtual == null
                ? usuarioRepository.existsByEmail(email)
                : usuarioRepository.existsByEmailAndIdUsuarioNot(email, idUsuarioAtual);

        if (emailEmUso) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email ja cadastrado."
            );
        }
    }
}
