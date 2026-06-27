package conne.connect.connect.Auth.service;

import conne.connect.connect.Auth.dto.LoginRequestDTO;
import conne.connect.connect.Auth.dto.LoginResponseDTO;
import conne.connect.connect.Empresa.enums.StatusEmpresa;
import conne.connect.connect.Empresa.model.EmpresaModel;
import conne.connect.connect.Usuario.enums.StatusUsuario;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.model.UsuarioModel;
import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;
import conne.connect.connect.Usuario.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UsuarioRepository usuarioRepository,
            UsuarioEmpresaRepository usuarioEmpresaRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioEmpresaRepository = usuarioEmpresaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        UsuarioModel usuario = usuarioRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Email ou senha invalidos."
                ));

        boolean senhaCorreta = passwordEncoder.matches(
                loginRequestDTO.getSenha(),
                usuario.getSenha()
        );

        if (!senhaCorreta) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Email ou senha invalidos."
            );
        }

        if (usuario.getStatus() != StatusUsuario.ativo) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Usuario inativo."
            );
        }

        UsuarioEmpresaModel usuarioEmpresa = usuarioEmpresaRepository
                .findFirstByIdUsuario_IdUsuarioAndAtivoTrueAndExcluidoIsNull(usuario.getIdUsuario())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Usuario nao possui vinculo ativo com uma empresa."
                ));

        EmpresaModel empresa = usuarioEmpresa.getIdEmpresa();

        if (empresa == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Empresa vinculada nao encontrada."
            );
        }

        if (empresa.getStatus() != StatusEmpresa.ativa || empresa.getExcluido() != null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Empresa vinculada nao esta ativa."
            );
        }

        return new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNome(),
                usuario.getEmail(),
                empresa.getIdEmpresa(),
                usuarioEmpresa.getIdUsuarioEmpresa(),
                usuarioEmpresa.getPapel().name(),
                usuario.getStatus().name()
        );
    }
}
