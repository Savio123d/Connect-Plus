package conne.connect.connect.Recompensa.Empresa.service;

import conne.connect.connect.Assinatura.dto.AssinaturaCadastroResultadoDTO;
import conne.connect.connect.Assinatura.service.AssinaturaService;
import conne.connect.connect.Recompensa.Empresa.dto.CadastroEmpresaDTO;
import conne.connect.connect.Recompensa.Empresa.dto.CadastroEmpresaResponseDTO;
import conne.connect.connect.Recompensa.Empresa.enums.StatusEmpresa;
import conne.connect.connect.Recompensa.Empresa.model.EmpresaModel;
import conne.connect.connect.Recompensa.Empresa.repository.EmpresaRepository;
import conne.connect.connect.Plano.enums.TipoPlano;
import conne.connect.connect.Usuario.dto.CadastroUsuarioEmpresaDTO;
import conne.connect.connect.Usuario.enums.PapelEmpresa;
import conne.connect.connect.Usuario.enums.StatusUsuario;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.model.UsuarioModel;
import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;
import conne.connect.connect.Usuario.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioEmpresaRepository usuarioEmpresaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AssinaturaService assinaturaService;

    @Transactional(readOnly = true)
    public List<EmpresaModel> findAll() {
        return empresaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<EmpresaModel> buscarPorId(Long idEmpresa) {
        return empresaRepository.findById(idEmpresa);
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

    public void excluirEmpresa(Long idEmpresa) {
        empresaRepository.deleteById(idEmpresa);
    }

    private EmpresaModel buscarEmpresaExistente(Long idEmpresa) {
        return empresaRepository.findById(idEmpresa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa nao encontrada."));
    }

    private void validarCnpjDisponivel(String cnpj) {
        if (cnpj != null && empresaRepository.existsByCnpj(cnpj)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CNPJ ja cadastrado.");
        }
    }

    private void validarEmailDisponivel(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ja cadastrado.");
        }
    }
}