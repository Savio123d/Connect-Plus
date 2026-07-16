package conne.connect.connect.Projeto.service;

import conne.connect.connect.Security.AutorizacaoService;
import conne.connect.connect.Projeto.model.ProjetoModel;
import conne.connect.connect.Projeto.repository.ProjetoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final AutorizacaoService autorizacaoService;

    public ProjetoService(ProjetoRepository projetoRepository, AutorizacaoService autorizacaoService) {
        this.projetoRepository = projetoRepository;
        this.autorizacaoService = autorizacaoService;
    }

    @Transactional(readOnly = true)
    public List<ProjetoModel> findAll() {
        return projetoRepository.findByIdEmpresa_IdEmpresaAndExcluidoIsNull(autorizacaoService.empresaAtual());
    }

    @Transactional
    public ProjetoModel criarProjeto(ProjetoModel projetoModel) {
        validarEscopo(projetoModel);
        return projetoRepository.save(projetoModel);
    }

    @Transactional(readOnly = true)
    public Optional<ProjetoModel> buscarPorId(Long idProjeto) {
        return projetoRepository.findByIdProjetoAndIdEmpresa_IdEmpresaAndExcluidoIsNull(idProjeto, autorizacaoService.empresaAtual());
    }

    @Transactional
    public ProjetoModel atualizarProjeto(Long idProjeto, ProjetoModel projetoModel) {
        validarEscopo(projetoModel);
        ProjetoModel projeto = buscarProjetoExistente(idProjeto);
        projeto.setIdGestorUsuarioEmpresa(projetoModel.getIdGestorUsuarioEmpresa());
        projeto.setNome(projetoModel.getNome());
        projeto.setDescricao(projetoModel.getDescricao());
        projeto.setDataInicio(projetoModel.getDataInicio());
        projeto.setDataFimPrevista(projetoModel.getDataFimPrevista());
        projeto.setDataFimReal(projetoModel.getDataFimReal());
        projeto.setStatus(projetoModel.getStatus());
        return projetoRepository.save(projeto);
    }

    @Transactional
    public void excluirProjeto(Long idProjeto) {
        ProjetoModel projeto = buscarProjetoExistente(idProjeto);
        projeto.setExcluido(LocalDate.now());
        projetoRepository.save(projeto);
    }

    private void validarEscopo(ProjetoModel registro) {
        autorizacaoService.validarEmpresaAtual(registro.getIdEmpresa() != null ? registro.getIdEmpresa().getIdEmpresa() : null);
        autorizacaoService.validarEmpresaAtual(registro.getIdGestorUsuarioEmpresa() != null
                && registro.getIdGestorUsuarioEmpresa().getIdEmpresa() != null
                ? registro.getIdGestorUsuarioEmpresa().getIdEmpresa().getIdEmpresa() : null);
    }

    private ProjetoModel buscarProjetoExistente(Long idProjeto) {
        return buscarPorId(idProjeto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado."));
    }
}
