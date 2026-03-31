package conne.connect.connect.Services;

import conne.connect.connect.Models.SaldoXpModel;
import conne.connect.connect.Repositories.SaldoXpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SaldoXpService {

    @Autowired
    private SaldoXpRepository saldoXpRepository;

    public List<SaldoXpModel> findAll() {
        return saldoXpRepository.findAll();
    }

    public SaldoXpModel criarSaldoXp(SaldoXpModel saldoXpModel) {
        return saldoXpRepository.save(saldoXpModel);
    }

    public Optional<SaldoXpModel> buscarPorId(Long idSaldoXp) {
        return saldoXpRepository.findById(idSaldoXp);
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
