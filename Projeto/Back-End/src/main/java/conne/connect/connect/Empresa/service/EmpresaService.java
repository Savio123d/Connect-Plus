package conne.connect.connect.Empresa.service;

import conne.connect.connect.Security.AutorizacaoService;
import conne.connect.connect.Assinatura.dto.AssinaturaCadastroResultadoDTO;
import conne.connect.connect.Assinatura.enums.StatusAssinatura;
import conne.connect.connect.Assinatura.model.AssinaturaModel;
import conne.connect.connect.Assinatura.repository.AssinaturaRepository;
import conne.connect.connect.Assinatura.service.AssinaturaService;
import conne.connect.connect.Empresa.dto.CadastroEmpresaDTO;
import conne.connect.connect.Empresa.dto.CadastroEmpresaResponseDTO;
import conne.connect.connect.Empresa.enums.StatusEmpresa;
import conne.connect.connect.Empresa.model.EmpresaModel;
import conne.connect.connect.Empresa.repository.EmpresaRepository;
import conne.connect.connect.Plano.enums.TipoPlano;
import conne.connect.connect.Plano.model.PlanoModel;
import conne.connect.connect.Usuario.dto.CadastroUsuarioEmpresaDTO;
import conne.connect.connect.Usuario.enums.PapelEmpresa;
import conne.connect.connect.Usuario.enums.StatusUsuario;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.model.UsuarioModel;
import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;
import conne.connect.connect.Usuario.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final PasswordEncoder passwordEncoder;
    private final AssinaturaService assinaturaService;
    private final AssinaturaRepository assinaturaRepository;
    private final AutorizacaoService autorizacaoService;

    public EmpresaService(
            EmpresaRepository empresaRepository,
            UsuarioRepository usuarioRepository,
            UsuarioEmpresaRepository usuarioEmpresaRepository,
            PasswordEncoder passwordEncoder,
            AssinaturaService assinaturaService,
            AssinaturaRepository assinaturaRepository,
            AutorizacaoService autorizacaoService
    ) {
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioEmpresaRepository = usuarioEmpresaRepository;
        this.passwordEncoder = passwordEncoder;
        this.assinaturaService = assinaturaService;
        this.assinaturaRepository = assinaturaRepository;
        this.autorizacaoService = autorizacaoService;
    }

    @Transactional(readOnly = true)
    public List<EmpresaModel> findAll() {
        Long idEmpresa = autorizacaoService.empresaAtual();
        if (idEmpresa == null) {
            return List.of();
        }

        return buscarPorId(idEmpresa).map(List::of).orElseGet(List::of);
    }

    @Transactional(readOnly = true)
    public Optional<EmpresaModel> buscarPorId(Long idEmpresa) {
        Long empresaAtual = autorizacaoService.empresaAtual();
        if (empresaAtual != null && !empresaAtual.equals(idEmpresa)) {
            return Optional.empty();
        }

        return empresaRepository.findById(idEmpresa)
                .filter(empresa -> empresa.getExcluido() == null);
    }

    @Transactional
    public CadastroEmpresaResponseDTO cadastrarEmpresa(CadastroEmpresaDTO dto) {
        validarCnpjDisponivel(dto.getCnpj());
        validarEmailDisponivel(dto.getEmailAdmin());

        EmpresaModel empresa = new EmpresaModel();
        empresa.setRazaoSocial(dto.getRazaoSocial());
        empresa.setNomeFantasia(dto.getNomeFantasia());
        empresa.setCnpj(dto.getCnpj());
        empresa.setCidade(dto.getCidade());
        empresa.setUf(dto.getUf());
        empresa.setStatus(StatusEmpresa.ativa);
        EmpresaModel empresaSalva = empresaRepository.save(empresa);

        UsuarioModel usuario = new UsuarioModel();
        usuario.setNome(dto.getNomeAdmin());
        usuario.setEmail(dto.getEmailAdmin());
        usuario.setSenha(passwordEncoder.encode(dto.getSenhaAdmin()));
        usuario.setStatus(StatusUsuario.ativo);
        UsuarioModel usuarioSalvo = usuarioRepository.save(usuario);

        UsuarioEmpresaModel usuarioEmpresa = new UsuarioEmpresaModel();
        usuarioEmpresa.setIdEmpresa(empresaSalva);
        usuarioEmpresa.setIdUsuario(usuarioSalvo);
        usuarioEmpresa.setPapel(PapelEmpresa.gestor);
        usuarioEmpresa.setAtivo(true);
        usuarioEmpresaRepository.save(usuarioEmpresa);

        TipoPlano tipoPlano = dto.getTipoPlano() != null ? dto.getTipoPlano() : TipoPlano.gratuito;
        AssinaturaCadastroResultadoDTO assinatura = assinaturaService.criarAssinaturaInicial(
                empresaSalva,
                tipoPlano,
                dto.getEmailAdmin());

        String mensagem = assinatura.getCheckoutUrl() != null
                ? "Empresa cadastrada. Conclua a assinatura Premium no Mercado Pago."
                : "Empresa e administrador cadastrados com sucesso.";

        return new CadastroEmpresaResponseDTO(
                empresaSalva.getIdEmpresa(),
                tipoPlano,
                assinatura.getStatusAssinatura(),
                assinatura.getCheckoutUrl(),
                mensagem);
    }

    @Transactional
    @CacheEvict(value = "usuariosPorEmpresa", allEntries = true)
    public void cadastrarUsuarioEmpresa(Long idEmpresa, CadastroUsuarioEmpresaDTO dto) {
        EmpresaModel empresa = buscarEmpresaExistente(idEmpresa);
        validarEmailDisponivel(dto.getEmail());
        validarLimiteDeUsuariosDoPlano(idEmpresa);

        UsuarioModel usuario = new UsuarioModel();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setStatus(StatusUsuario.ativo);
        UsuarioModel usuarioSalvo = usuarioRepository.save(usuario);

        UsuarioEmpresaModel usuarioEmpresa = new UsuarioEmpresaModel();
        usuarioEmpresa.setIdEmpresa(empresa);
        usuarioEmpresa.setIdUsuario(usuarioSalvo);
        usuarioEmpresa.setPapel(dto.getPapel() != null ? dto.getPapel() : PapelEmpresa.colaborador);
        usuarioEmpresa.setAtivo(true);
        usuarioEmpresaRepository.save(usuarioEmpresa);
    }

    @Transactional
    @CacheEvict(value = "vinculoAutenticacao", allEntries = true)
    public EmpresaModel atualizarEmpresa(Long idEmpresa, EmpresaModel empresaModel) {
        EmpresaModel empresa = buscarEmpresaExistente(idEmpresa);

        if (empresaModel.getRazaoSocial() != null) {
            empresa.setRazaoSocial(empresaModel.getRazaoSocial());
        }

        if (empresaModel.getNomeFantasia() != null) {
            empresa.setNomeFantasia(empresaModel.getNomeFantasia());
        }

        if (empresaModel.getCnpj() != null) {
            empresa.setCnpj(empresaModel.getCnpj());
        }

        if (empresaModel.getCidade() != null) {
            empresa.setCidade(empresaModel.getCidade());
        }

        if (empresaModel.getUf() != null) {
            empresa.setUf(empresaModel.getUf());
        }

        if (empresaModel.getStatus() != null) {
            empresa.setStatus(empresaModel.getStatus());
        }

        return empresaRepository.save(empresa);
    }

    @Transactional
    @CacheEvict(value = "vinculoAutenticacao", allEntries = true)
    public void excluirEmpresa(Long idEmpresa) {
        EmpresaModel empresa = buscarEmpresaExistente(idEmpresa);
        empresa.setStatus(StatusEmpresa.inativa);
        empresa.setExcluido(LocalDate.now());
        empresaRepository.save(empresa);
    }

    private EmpresaModel buscarEmpresaExistente(Long idEmpresa) {
        return buscarPorId(idEmpresa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada."));
    }

    private void validarLimiteDeUsuariosDoPlano(Long idEmpresa) {
        AssinaturaModel assinatura = assinaturaRepository
                .findFirstByIdEmpresa_IdEmpresaAndExcluidoIsNullOrderByIdAssinaturaDesc(idEmpresa)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Empresa não possui assinatura."
                ));

        if (assinatura.getStatus() != StatusAssinatura.ativa) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Assinatura precisa estar ativa para adicionar usuários."
            );
        }

        PlanoModel plano = assinatura.getIdPlano();
        Integer maxUsuarios = plano != null ? plano.getMaxUsuarios() : null;
        if (maxUsuarios == null || maxUsuarios < 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Plano não possui limite de usuários válido."
            );
        }

        Long usuariosAtivos = usuarioEmpresaRepository.countUsuariosAtivosPorEmpresa(idEmpresa);
        if (usuariosAtivos != null && usuariosAtivos >= maxUsuarios) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Limite de usuários do plano atingido (" + maxUsuarios + ")."
            );
        }
    }

    private void validarCnpjDisponivel(String cnpj) {
        if (cnpj != null && empresaRepository.existsByCnpj(cnpj)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CNPJ já cadastrado.");
        }
    }

    private void validarEmailDisponivel(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado.");
        }
    }
}