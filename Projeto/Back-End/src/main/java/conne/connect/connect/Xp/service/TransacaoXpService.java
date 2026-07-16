package conne.connect.connect.Xp.service;

import conne.connect.connect.Security.AutorizacaoService;
import conne.connect.connect.Xp.model.TransacaoXpModel;
import conne.connect.connect.Xp.repository.TransacaoXpRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TransacaoXpService {

    private final TransacaoXpRepository transacaoXpRepository;
    private final AutorizacaoService autorizacaoService;

    public TransacaoXpService(TransacaoXpRepository transacaoXpRepository, AutorizacaoService autorizacaoService) {
        this.transacaoXpRepository = transacaoXpRepository;
        this.autorizacaoService = autorizacaoService;
    }

    @Transactional(readOnly = true)
    public List<TransacaoXpModel> findAll() {
        return transacaoXpRepository.findByIdEmpresa_IdEmpresaAndExcluidoIsNull(autorizacaoService.empresaAtual());
    }

    @Transactional
    public TransacaoXpModel criarTransacaoXp(TransacaoXpModel transacaoXpModel) {
        validarEscopo(transacaoXpModel);
        return transacaoXpRepository.save(transacaoXpModel);
    }

    @Transactional(readOnly = true)
    public Optional<TransacaoXpModel> buscarPorId(Long idTransacaoXp) {
        return transacaoXpRepository.findByIdTransacaoXpAndIdEmpresa_IdEmpresaAndExcluidoIsNull(idTransacaoXp, autorizacaoService.empresaAtual());
    }

    @Transactional
    public TransacaoXpModel atualizarTransacaoXp(Long idTransacaoXp, TransacaoXpModel transacaoXpModel) {
        validarEscopo(transacaoXpModel);
        TransacaoXpModel transacaoXp = buscarTransacaoExistente(idTransacaoXp);
        transacaoXp.setIdUsuarioEmpresa(transacaoXpModel.getIdUsuarioEmpresa());
        transacaoXp.setIdTarefa(transacaoXpModel.getIdTarefa());
        transacaoXp.setIdRecompensa(transacaoXpModel.getIdRecompensa());
        transacaoXp.setTipo(transacaoXpModel.getTipo());
        transacaoXp.setValor(transacaoXpModel.getValor());
        transacaoXp.setObservacao(transacaoXpModel.getObservacao());
        return transacaoXpRepository.save(transacaoXp);
    }

    @Transactional
    public void excluirTransacaoXp(Long idTransacaoXp) {
        TransacaoXpModel transacaoXp = buscarTransacaoExistente(idTransacaoXp);
        transacaoXp.setExcluido(LocalDate.now());
        transacaoXpRepository.save(transacaoXp);
    }

    private void validarEscopo(TransacaoXpModel registro) {
        autorizacaoService.validarEmpresaAtual(registro.getIdEmpresa() != null ? registro.getIdEmpresa().getIdEmpresa() : null);
        autorizacaoService.validarEmpresaAtual(registro.getIdUsuarioEmpresa() != null
                && registro.getIdUsuarioEmpresa().getIdEmpresa() != null
                ? registro.getIdUsuarioEmpresa().getIdEmpresa().getIdEmpresa() : null);
        if (registro.getIdTarefa() != null) {
            autorizacaoService.validarEmpresaAtual(registro.getIdTarefa().getIdEmpresa() != null
                    ? registro.getIdTarefa().getIdEmpresa().getIdEmpresa() : null);
        }
        if (registro.getIdRecompensa() != null) {
            autorizacaoService.validarEmpresaAtual(registro.getIdRecompensa().getIdEmpresa() != null
                    ? registro.getIdRecompensa().getIdEmpresa().getIdEmpresa() : null);
        }
    }

    private TransacaoXpModel buscarTransacaoExistente(Long idTransacaoXp) {
        return buscarPorId(idTransacaoXp)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transação de XP não encontrada."));
    }
}
