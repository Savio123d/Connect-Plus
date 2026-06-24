package conne.connect.connect.Services;

import conne.connect.connect.Dto.LoginRequestDTO;
import conne.connect.connect.Dto.LoginResponseDTO;
import conne.connect.connect.Enums.StatusEmpresa;
import conne.connect.connect.Enums.StatusUsuario;
import conne.connect.connect.Models.EmpresaModel;
import conne.connect.connect.Models.UsuarioEmpresaModel;
import conne.connect.connect.Models.UsuarioModel;
import conne.connect.connect.Repositories.UsuarioEmpresaRepository;
import conne.connect.connect.Repositories.UsuarioRepository;
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
                        "Email ou senha inválidos."
                ));

        boolean senhaCorreta = passwordEncoder.matches(
                loginRequestDTO.getSenha(),
                usuario.getSenha()
        );

        if (!senhaCorreta) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Email ou senha inválidos."
            );
        }

        if (usuario.getStatus() != StatusUsuario.ativo) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Usuário inativo."
            );
        }

        UsuarioEmpresaModel usuarioEmpresa = usuarioEmpresaRepository
                .findFirstByIdUsuario_IdUsuarioAndAtivoTrueAndExcluidoIsNull(usuario.getIdUsuario())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Usuário não possui vínculo ativo com uma empresa."
                ));

        EmpresaModel empresa = usuarioEmpresa.getIdEmpresa();

        if (empresa == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Empresa vinculada não encontrada."
            );
        }

        if (empresa.getStatus() != StatusEmpresa.ativa || empresa.getExcluido() != null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Empresa vinculada não está ativa."
            );
        }

        UsuarioEmpresaModel usuarioEmpresa = usuarioEmpresaRepository
                .findFirstByIdUsuario_IdUsuarioAndAtivoTrueAndExcluidoIsNull(
                        usuario.getIdUsuario()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Usuário não possui vínculo ativo com uma empresa."
                ));

        UsuarioDTO usuarioDTO = UsuarioDTO.fromUsuarioEmpresa(usuarioEmpresa, null);

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