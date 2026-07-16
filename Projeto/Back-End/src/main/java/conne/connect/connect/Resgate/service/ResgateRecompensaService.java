package conne.connect.connect.Resgate.service;

import conne.connect.connect.Security.AutorizacaoService;
import conne.connect.connect.Resgate.model.ResgateRecompensaModel;
import conne.connect.connect.Resgate.repository.ResgateRecompensaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ResgateRecompensaService {

    private final ResgateRecompensaRepository resgateRecompensaRepository;
    private final AutorizacaoService autorizacaoService;

    public ResgateRecompensaService(ResgateRecompensaRepository resgateRecompensaRepository, AutorizacaoService autorizacaoService) {
        this.resgateRecompensaRepository = resgateRecompensaRepository;
        this.autorizacaoService = autorizacaoService;
    }

    @Transactional(readOnly = true)
    public List<ResgateRecompensaModel> findAll() {
        return resgateRecompensaRepository.findByIdEmpresa_IdEmpresaAndExcluidoIsNull(autorizacaoService.empresaAtual());
    }

    @Transactional
    public ResgateRecompensaModel criarResgateRecompensa(ResgateRecompensaModel resgateRecompensaModel) {
        validarEscopo(resgateRecompensaModel);
        return resgateRecompensaRepository.save(resgateRecompensaModel);
    }

    @Transactional(readOnly = true)
    public Optional<ResgateRecompensaModel> buscarPorId(Long idResgateRecompensa) {
        return resgateRecompensaRepository.findByIdResgateRecompensaAndIdEmpresa_IdEmpresaAndExcluidoIsNull(idResgateRecompensa, autorizacaoService.empresaAtual());
    }

    @Transactional
    public ResgateRecompensaModel atualizarResgateRecompensa(Long idResgateRecompensa, ResgateRecompensaModel resgateRecompensaModel) {
        validarEscopo(resgateRecompensaModel);
        ResgateRecompensaModel resgateRecompensa = buscarResgateExistente(idResgateRecompensa);
        resgateRecompensa.setIdUsuarioEmpresa(resgateRecompensaModel.getIdUsuarioEmpresa());
        resgateRecompensa.setIdRecompensa(resgateRecompensaModel.getIdRecompensa());
        resgateRecompensa.setIdTransacaoXp(resgateRecompensaModel.getIdTransacaoXp());
        resgateRecompensa.setQuantidade(resgateRecompensaModel.getQuantidade());
        resgateRecompensa.setXpGasto(resgateRecompensaModel.getXpGasto());
        resgateRecompensa.setStatus(resgateRecompensaModel.getStatus());
        return resgateRecompensaRepository.save(resgateRecompensa);
    }

    @Transactional
    public void excluirResgateRecompensa(Long idResgateRecompensa) {
        ResgateRecompensaModel resgateRecompensa = buscarResgateExistente(idResgateRecompensa);
        resgateRecompensa.setExcluido(LocalDate.now());
        resgateRecompensaRepository.save(resgateRecompensa);
    }

    private void validarEscopo(ResgateRecompensaModel registro) {
        autorizacaoService.validarEmpresaAtual(registro.getIdEmpresa() != null ? registro.getIdEmpresa().getIdEmpresa() : null);
        autorizacaoService.validarEmpresaAtual(registro.getIdUsuarioEmpresa() != null
                && registro.getIdUsuarioEmpresa().getIdEmpresa() != null
                ? registro.getIdUsuarioEmpresa().getIdEmpresa().getIdEmpresa() : null);
        autorizacaoService.validarEmpresaAtual(registro.getIdRecompensa() != null
                && registro.getIdRecompensa().getIdEmpresa() != null
                ? registro.getIdRecompensa().getIdEmpresa().getIdEmpresa() : null);
        if (registro.getIdTransacaoXp() != null) {
            autorizacaoService.validarEmpresaAtual(registro.getIdTransacaoXp().getIdEmpresa() != null
                    ? registro.getIdTransacaoXp().getIdEmpresa().getIdEmpresa() : null);
        }
    }

    private ResgateRecompensaModel buscarResgateExistente(Long idResgateRecompensa) {
        return buscarPorId(idResgateRecompensa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resgate de recompensa não encontrado."));
    }
}
