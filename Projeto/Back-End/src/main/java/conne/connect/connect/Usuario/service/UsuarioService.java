package conne.connect.connect.Usuario.service;

import conne.connect.connect.Security.AutorizacaoService;
import conne.connect.connect.Usuario.dto.AlterarSenhaRequestDTO;
import conne.connect.connect.Usuario.dto.UsuarioDTO;
import conne.connect.connect.Usuario.dto.UsuarioRequestDTO;
import conne.connect.connect.Usuario.enums.StatusUsuario;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.model.UsuarioModel;
import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;
import conne.connect.connect.Usuario.repository.UsuarioRepository;
import conne.connect.connect.Xp.model.SaldoXpModel;
import conne.connect.connect.Xp.repository.SaldoXpRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.CacheEvict;
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
    private final AutorizacaoService autorizacaoService;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            UsuarioEmpresaRepository usuarioEmpresaRepository,
            SaldoXpRepository saldoXpRepository,
            PasswordEncoder passwordEncoder,
            AutorizacaoService autorizacaoService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioEmpresaRepository = usuarioEmpresaRepository;
        this.saldoXpRepository = saldoXpRepository;
        this.passwordEncoder = passwordEncoder;
        this.autorizacaoService = autorizacaoService;
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> findAll() {
        Long idEmpresa = autorizacaoService.empresaAtual();
        return idEmpresa == null ? List.of() : listarUsuariosDaEmpresa(idEmpresa);
    }

    @Cacheable(value = "usuariosPorEmpresa", key = "#idEmpresa")
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarUsuariosDaEmpresa(Long idEmpresa) {
        List<UsuarioEmpresaModel> vinculos =
                usuarioEmpresaRepository.findByIdEmpresa_IdEmpresaAndExcluidoIsNull(idEmpresa);

        // Um unico SELECT de saldos da empresa evita uma consulta por usuario (N+1)
        // e garante que o saldo exibido pertence a esta empresa.
        Map<Long, SaldoXpModel> saldosPorVinculo = saldoXpRepository
                .findByIdEmpresa_IdEmpresaAndExcluidoIsNull(idEmpresa)
                .stream()
                .filter(saldo -> saldo.getIdUsuarioEmpresa() != null
                        && saldo.getIdUsuarioEmpresa().getIdUsuarioEmpresa() != null)
                .collect(Collectors.toMap(
                        saldo -> saldo.getIdUsuarioEmpresa().getIdUsuarioEmpresa(),
                        Function.identity(),
                        (primeiro, segundo) -> primeiro));

        return vinculos.stream()
                .map(vinculo -> UsuarioDTO.fromUsuarioEmpresa(
                        vinculo,
                        saldosPorVinculo.get(vinculo.getIdUsuarioEmpresa())))
                .toList();
    }

    @Transactional
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

    @Transactional
    public UsuarioDTO atualizarUsuario(Long idUsuario, UsuarioRequestDTO usuarioRequestDTO){
        UsuarioModel usuario = buscarUsuarioExistente(idUsuario);

        validarEmailDisponivel(usuarioRequestDTO.getEmail(), idUsuario);

        usuarioRequestDTO.applyToModel(usuario);

        usuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha()));

        return UsuarioDTO.fromModel(usuarioRepository.save(usuario));
    }

    // Exclusao logica: usuario vira inativo e os vinculos com empresas sao desativados,
    // preservando historico de tarefas, XP e resgates.
    @Transactional
    @CacheEvict(value = "usuariosPorEmpresa", allEntries = true)
    public void excluirUsuario(Long idUsuario){
        UsuarioModel usuario = buscarUsuarioExistente(idUsuario);
        usuario.setStatus(StatusUsuario.inativo);
        usuarioRepository.save(usuario);

        List<UsuarioEmpresaModel> vinculos =
                usuarioEmpresaRepository.findAllByIdUsuario_IdUsuarioAndExcluidoIsNull(idUsuario);

        for (UsuarioEmpresaModel vinculo : vinculos) {
            vinculo.setAtivo(false);
            vinculo.setExcluido(LocalDate.now());
        }

        usuarioEmpresaRepository.saveAll(vinculos);
    }


    @Transactional
    public void alterarSenha(Long idUsuario, AlterarSenhaRequestDTO request) {
        UsuarioModel usuario = buscarUsuarioExistente(idUsuario);

        if (!passwordEncoder.matches(request.getSenhaAtual(), usuario.getSenha())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Senha atual incorreta."
            );
        }

        usuario.setSenha(passwordEncoder.encode(request.getNovaSenha()));
        usuarioRepository.save(usuario);
    }

    private UsuarioModel buscarUsuarioExistente(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado."
                ));
    }

    private void validarEmailDisponivel(String email, Long idUsuarioAtual) {
        boolean emailEmUso = idUsuarioAtual == null
                ? usuarioRepository.existsByEmail(email)
                : usuarioRepository.existsByEmailAndIdUsuarioNot(email, idUsuarioAtual);

        if (emailEmUso) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "E-mail já cadastrado."
            );
        }
    }
}
