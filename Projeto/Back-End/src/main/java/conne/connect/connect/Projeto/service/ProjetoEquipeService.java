package conne.connect.connect.Projeto.service;

import conne.connect.connect.Security.AutorizacaoService;
import conne.connect.connect.Projeto.model.ProjetoEquipeModel;
import conne.connect.connect.Projeto.repository.ProjetoEquipeRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProjetoEquipeService {

    private final ProjetoEquipeRepository projetoEquipeRepository;
    private final AutorizacaoService autorizacaoService;

    public ProjetoEquipeService(ProjetoEquipeRepository projetoEquipeRepository, AutorizacaoService autorizacaoService) {
        this.projetoEquipeRepository = projetoEquipeRepository;
        this.autorizacaoService = autorizacaoService;
    }

    @Transactional(readOnly = true)
    public List<ProjetoEquipeModel> findAll() {
        return projetoEquipeRepository.findByIdEmpresa_IdEmpresaAndExcluidoIsNull(autorizacaoService.empresaAtual());
    }

    @Transactional
    public ProjetoEquipeModel criarProjetoEquipe(ProjetoEquipeModel projetoEquipeModel) {
        validarEscopo(projetoEquipeModel);
        return projetoEquipeRepository.save(projetoEquipeModel);
    }

    @Transactional(readOnly = true)
    public Optional<ProjetoEquipeModel> buscarPorId(Long idProjetoEquipe) {
        return projetoEquipeRepository.findByIdProjetoEquipeAndIdEmpresa_IdEmpresaAndExcluidoIsNull(idProjetoEquipe, autorizacaoService.empresaAtual());
    }

    @Transactional
    public ProjetoEquipeModel atualizarProjetoEquipe(Long idProjetoEquipe, ProjetoEquipeModel projetoEquipeModel) {
        validarEscopo(projetoEquipeModel);
        ProjetoEquipeModel projetoEquipe = buscarProjetoEquipeExistente(idProjetoEquipe);
        projetoEquipe.setIdProjeto(projetoEquipeModel.getIdProjeto());
        projetoEquipe.setIdEquipe(projetoEquipeModel.getIdEquipe());
        return projetoEquipeRepository.save(projetoEquipe);
    }

    @Transactional
    public void excluirProjetoEquipe(Long idProjetoEquipe) {
        ProjetoEquipeModel projetoEquipe = buscarProjetoEquipeExistente(idProjetoEquipe);
        projetoEquipe.setExcluido(LocalDate.now());
        projetoEquipeRepository.save(projetoEquipe);
    }

    private void validarEscopo(ProjetoEquipeModel registro) {
        autorizacaoService.validarEmpresaAtual(registro.getIdEmpresa() != null ? registro.getIdEmpresa().getIdEmpresa() : null);
        autorizacaoService.validarEmpresaAtual(registro.getIdProjeto() != null && registro.getIdProjeto().getIdEmpresa() != null
                ? registro.getIdProjeto().getIdEmpresa().getIdEmpresa() : null);
        autorizacaoService.validarEmpresaAtual(registro.getIdEquipe() != null && registro.getIdEquipe().getIdEmpresa() != null
                ? registro.getIdEquipe().getIdEmpresa().getIdEmpresa() : null);
    }

    private ProjetoEquipeModel buscarProjetoEquipeExistente(Long idProjetoEquipe) {
        return buscarPorId(idProjetoEquipe)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vínculo projeto-equipe não encontrado."));
    }
}
