package conne.connect.connect.Auth.service;

import conne.connect.connect.Auth.dto.LoginRequestDTO;
import conne.connect.connect.Auth.dto.LoginResponseDTO;
import conne.connect.connect.Empresa.enums.StatusEmpresa;
import conne.connect.connect.Empresa.model.EmpresaModel;
import conne.connect.connect.Security.TokenService;
import conne.connect.connect.Usuario.enums.StatusUsuario;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.model.UsuarioModel;
import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;
import conne.connect.connect.Usuario.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            UsuarioEmpresaRepository usuarioEmpresaRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioEmpresaRepository = usuarioEmpresaRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        UsuarioModel usuario = usuarioRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(this::credenciaisInvalidas);

        boolean senhaCorreta = passwordEncoder.matches(
                loginRequestDTO.getSenha(),
                usuario.getSenha()
        );

        if (!senhaCorreta) {
            throw credenciaisInvalidas();
        }

        if (usuario.getStatus() != StatusUsuario.ativo) {
            throw credenciaisInvalidas();
        }

        UsuarioEmpresaModel usuarioEmpresa = usuarioEmpresaRepository
                .findFirstByIdUsuario_IdUsuarioAndAtivoTrueAndExcluidoIsNull(usuario.getIdUsuario())
                .orElseThrow(this::credenciaisInvalidas);

        EmpresaModel empresa = usuarioEmpresa.getIdEmpresa();

        if (empresa == null) {
            throw credenciaisInvalidas();
        }

        if (empresa.getStatus() != StatusEmpresa.ativa || empresa.getExcluido() != null) {
            throw credenciaisInvalidas();
        }

        LoginResponseDTO resposta = new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNome(),
                usuario.getEmail(),
                empresa.getIdEmpresa(),
                usuarioEmpresa.getIdUsuarioEmpresa(),
                usuarioEmpresa.getPapel().name(),
                usuario.getStatus().name()
        );

        resposta.setToken(tokenService.gerarToken(
                usuario.getIdUsuario(),
                empresa.getIdEmpresa(),
                usuarioEmpresa.getIdUsuarioEmpresa(),
                usuarioEmpresa.getPapel().name()
        ));

        return resposta;
    }

    private ResponseStatusException credenciaisInvalidas() {
        return new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "E-mail ou senha inválidos."
        );
    }
}
