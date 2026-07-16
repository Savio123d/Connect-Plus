package conne.connect.connect.Usuario.service;

import conne.connect.connect.Security.AutorizacaoService;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioEmpresaService {

    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final AutorizacaoService autorizacaoService;

    public UsuarioEmpresaService(
            UsuarioEmpresaRepository usuarioEmpresaRepository,
            AutorizacaoService autorizacaoService
    ) {
        this.usuarioEmpresaRepository = usuarioEmpresaRepository;
        this.autorizacaoService = autorizacaoService;
    }

    @Transactional(readOnly = true)
    public List<UsuarioEmpresaModel> findAll() {
        return usuarioEmpresaRepository.findByIdEmpresa_IdEmpresaAndExcluidoIsNull(autorizacaoService.empresaAtual());
    }

    @Transactional
    @CacheEvict(value = "usuariosPorEmpresa", allEntries = true)
    public UsuarioEmpresaModel criarUsuarioEmpresa(UsuarioEmpresaModel usuarioEmpresaModel) {
        validarEmpresaDoVinculo(usuarioEmpresaModel);
        return usuarioEmpresaRepository.save(usuarioEmpresaModel);
    }

    @Transactional(readOnly = true)
    public Optional<UsuarioEmpresaModel> buscarPorId(Long idUsuarioEmpresa) {
        return usuarioEmpresaRepository.findByIdUsuarioEmpresaAndAtivoTrueAndExcluidoIsNull(idUsuarioEmpresa);
    }

    @Transactional
    @CacheEvict(value = "usuariosPorEmpresa", allEntries = true)
    public UsuarioEmpresaModel atualizarUsuarioEmpresa(Long idUsuarioEmpresa, UsuarioEmpresaModel usuarioEmpresaModel) {
        UsuarioEmpresaModel usuarioEmpresa = buscarVinculoExistente(idUsuarioEmpresa);
        validarSetorDaEmpresa(usuarioEmpresaModel);
        usuarioEmpresa.setIdSetor(usuarioEmpresaModel.getIdSetor());
        usuarioEmpresa.setPapel(usuarioEmpresaModel.getPapel());
        usuarioEmpresa.setAtivo(usuarioEmpresaModel.getAtivo());
        return usuarioEmpresaRepository.save(usuarioEmpresa);
    }

    @Transactional
    @CacheEvict(value = "usuariosPorEmpresa", allEntries = true)
    public void excluirUsuarioEmpresa(Long idUsuarioEmpresa) {
        UsuarioEmpresaModel usuarioEmpresa = buscarVinculoExistente(idUsuarioEmpresa);
        usuarioEmpresa.setAtivo(false);
        usuarioEmpresa.setExcluido(LocalDate.now());
        usuarioEmpresaRepository.save(usuarioEmpresa);
    }

    private UsuarioEmpresaModel buscarVinculoExistente(Long idUsuarioEmpresa) {
        return buscarPorId(idUsuarioEmpresa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vínculo usuário-empresa não encontrado."));
    }

    private void validarEmpresaDoVinculo(UsuarioEmpresaModel usuarioEmpresa) {
        if (usuarioEmpresa.getIdEmpresa() == null
                || !autorizacaoService.mesmaEmpresa(usuarioEmpresa.getIdEmpresa().getIdEmpresa())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "O v?nculo deve pertencer ? empresa autenticada."
            );
        }

        validarSetorDaEmpresa(usuarioEmpresa);
    }

    private void validarSetorDaEmpresa(UsuarioEmpresaModel usuarioEmpresa) {
        if (usuarioEmpresa.getIdSetor() != null
                && (usuarioEmpresa.getIdSetor().getIdEmpresa() == null
                || !autorizacaoService.mesmaEmpresa(
                        usuarioEmpresa.getIdSetor().getIdEmpresa().getIdEmpresa()))) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "O setor deve pertencer ? empresa autenticada."
            );
        }
    }
}
