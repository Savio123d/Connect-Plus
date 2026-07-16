package conne.connect.connect.Equipe.service;

import conne.connect.connect.Security.AutorizacaoService;
import conne.connect.connect.Equipe.model.EquipeModel;
import conne.connect.connect.Equipe.repository.EquipeRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EquipeService {

    private final EquipeRepository equipeRepository;
    private final AutorizacaoService autorizacaoService;

    public EquipeService(EquipeRepository equipeRepository, AutorizacaoService autorizacaoService) {
        this.equipeRepository = equipeRepository;
        this.autorizacaoService = autorizacaoService;
    }

    @Transactional(readOnly = true)
    public List<EquipeModel> findAll() {
        return equipeRepository.findByIdEmpresa_IdEmpresaAndExcluidoIsNull(autorizacaoService.empresaAtual());
    }

    @Transactional
    public EquipeModel criarEquipe(EquipeModel equipeModel) {
        validarEscopo(equipeModel);
        return equipeRepository.save(equipeModel);
    }

    @Transactional(readOnly = true)
    public Optional<EquipeModel> buscarPorId(Long idEquipe) {
        return equipeRepository.findByIdEquipeAndIdEmpresa_IdEmpresaAndExcluidoIsNull(idEquipe, autorizacaoService.empresaAtual());
    }

    @Transactional
    public EquipeModel atualizarEquipe(Long idEquipe, EquipeModel equipeModel) {
        validarEscopo(equipeModel);
        EquipeModel equipe = buscarEquipeExistente(idEquipe);
        equipe.setNome(equipeModel.getNome());
        equipe.setDescricao(equipeModel.getDescricao());
        equipe.setAtivo(equipeModel.getAtivo());
        return equipeRepository.save(equipe);
    }

    @Transactional
    public void excluirEquipe(Long idEquipe) {
        EquipeModel equipe = buscarEquipeExistente(idEquipe);
        equipe.setExcluido(LocalDate.now());
        equipeRepository.save(equipe);
    }

    private void validarEscopo(EquipeModel registro) {
        autorizacaoService.validarEmpresaAtual(
                registro.getIdEmpresa() != null ? registro.getIdEmpresa().getIdEmpresa() : null
        );
    }

    private EquipeModel buscarEquipeExistente(Long idEquipe) {
        return buscarPorId(idEquipe)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipe não encontrada."));
    }
}
