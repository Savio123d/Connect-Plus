package conne.connect.connect.Xp.service;

import conne.connect.connect.Security.AutorizacaoService;
import conne.connect.connect.Xp.model.SaldoXpModel;
import conne.connect.connect.Xp.repository.SaldoXpRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SaldoXpService {

    private final SaldoXpRepository saldoXpRepository;
    private final AutorizacaoService autorizacaoService;

    public SaldoXpService(SaldoXpRepository saldoXpRepository, AutorizacaoService autorizacaoService) {
        this.saldoXpRepository = saldoXpRepository;
        this.autorizacaoService = autorizacaoService;
    }

    @Transactional(readOnly = true)
    public List<SaldoXpModel> findAll() {
        return saldoXpRepository.findByIdEmpresa_IdEmpresaAndExcluidoIsNull(autorizacaoService.empresaAtual());
    }

    @Transactional
    public SaldoXpModel criarSaldoXp(SaldoXpModel saldoXpModel) {
        validarEscopo(saldoXpModel);
        return saldoXpRepository.save(saldoXpModel);
    }

    @Transactional(readOnly = true)
    public Optional<SaldoXpModel> buscarPorId(Long idSaldoXp) {
        return saldoXpRepository.findByIdSaldoXpAndIdEmpresa_IdEmpresaAndExcluidoIsNull(idSaldoXp, autorizacaoService.empresaAtual());
    }

    @Transactional(readOnly = true)
    public Integer buscarSaldoPorUsuarioEmpresa(Long idUsuarioEmpresa) {
        if (idUsuarioEmpresa == null || idUsuarioEmpresa < 1) {
            return 0;
        }

        autorizacaoService.validarAcessoAoVinculo(idUsuarioEmpresa);

        return saldoXpRepository
                .findByIdUsuarioEmpresa_IdUsuarioEmpresaAndIdEmpresa_IdEmpresaAndExcluidoIsNull(
                        idUsuarioEmpresa,
                        autorizacaoService.empresaAtual()
                )
                .map(SaldoXpModel::getXpTotal)
                .orElse(0);
    }

    @Transactional
    public SaldoXpModel atualizarSaldoXp(Long idSaldoXp, SaldoXpModel saldoXpModel) {
        validarEscopo(saldoXpModel);
        SaldoXpModel saldoXp = buscarSaldoExistente(idSaldoXp);
        saldoXp.setIdUsuarioEmpresa(saldoXpModel.getIdUsuarioEmpresa());
        saldoXp.setXpTotal(saldoXpModel.getXpTotal());
        return saldoXpRepository.save(saldoXp);
    }

    @Transactional
    public void excluirSaldoXp(Long idSaldoXp) {
        SaldoXpModel saldoXp = buscarSaldoExistente(idSaldoXp);
        saldoXp.setExcluido(LocalDate.now());
        saldoXpRepository.save(saldoXp);
    }

    private void validarEscopo(SaldoXpModel registro) {
        autorizacaoService.validarEmpresaAtual(registro.getIdEmpresa() != null ? registro.getIdEmpresa().getIdEmpresa() : null);
        autorizacaoService.validarEmpresaAtual(registro.getIdUsuarioEmpresa() != null
                && registro.getIdUsuarioEmpresa().getIdEmpresa() != null
                ? registro.getIdUsuarioEmpresa().getIdEmpresa().getIdEmpresa() : null);
    }

    private SaldoXpModel buscarSaldoExistente(Long idSaldoXp) {
        return buscarPorId(idSaldoXp)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Saldo de XP não encontrado."));
    }
}
