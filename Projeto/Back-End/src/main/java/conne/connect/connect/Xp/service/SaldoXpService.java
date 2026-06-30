package conne.connect.connect.Xp.service;

import conne.connect.connect.Xp.model.SaldoXpModel;
import conne.connect.connect.Xp.repository.SaldoXpRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaldoXpService {

    @Autowired
    private SaldoXpRepository saldoXpRepository;

    @Transactional(readOnly = true)
    public List<SaldoXpModel> findAll() {
        return saldoXpRepository.findAll();
    }

    public SaldoXpModel criarSaldoXp(SaldoXpModel saldoXpModel) {
        return saldoXpRepository.save(saldoXpModel);
    }

    @Transactional(readOnly = true)
    public Optional<SaldoXpModel> buscarPorId(Long idSaldoXp) {
        return saldoXpRepository.findById(idSaldoXp);
    }

    @Transactional(readOnly = true)
    public Integer buscarSaldoPorUsuarioEmpresa(Long idUsuarioEmpresa) {
        if (idUsuarioEmpresa == null || idUsuarioEmpresa < 1) {
            return 0;
        }

        return saldoXpRepository
                .findByIdUsuarioEmpresa_IdUsuarioEmpresa(idUsuarioEmpresa)
                .map(SaldoXpModel::getXpTotal)
                .orElse(0);
    }

    public SaldoXpModel atualizarSaldoXp(Long idSaldoXp, SaldoXpModel saldoXpModel) {
        SaldoXpModel saldoXp = saldoXpRepository.findById(idSaldoXp).get();
        saldoXp.setIdEmpresa(saldoXpModel.getIdEmpresa());
        saldoXp.setIdUsuarioEmpresa(saldoXpModel.getIdUsuarioEmpresa());
        saldoXp.setXpTotal(saldoXpModel.getXpTotal());
        return saldoXpRepository.save(saldoXp);
    }

    public void excluirSaldoXp(Long idSaldoXp) {
        saldoXpRepository.deleteById(idSaldoXp);
    }
}
