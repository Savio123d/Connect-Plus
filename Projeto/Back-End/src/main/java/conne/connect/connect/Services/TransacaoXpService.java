package conne.connect.connect.Services;

import conne.connect.connect.Models.TransacaoXpModel;
import conne.connect.connect.Repositories.TransacaoXpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransacaoXpService {

    @Autowired
    private TransacaoXpRepository transacaoXpRepository;

    public List<TransacaoXpModel> findAll() {
        return transacaoXpRepository.findAll();
    }

    public TransacaoXpModel criarTransacaoXp(TransacaoXpModel transacaoXpModel) {
        return transacaoXpRepository.save(transacaoXpModel);
    }

    public Optional<TransacaoXpModel> buscarPorId(Long idTransacaoXp) {
        return transacaoXpRepository.findById(idTransacaoXp);
    }

    public TransacaoXpModel atualizarTransacaoXp(Long idTransacaoXp, TransacaoXpModel transacaoXpModel) {
        TransacaoXpModel transacaoXp = transacaoXpRepository.findById(idTransacaoXp).get();
        transacaoXp.setIdEmpresa(transacaoXpModel.getIdEmpresa());
        transacaoXp.setIdUsuarioEmpresa(transacaoXpModel.getIdUsuarioEmpresa());
        transacaoXp.setIdTarefa(transacaoXpModel.getIdTarefa());
        transacaoXp.setIdRecompensa(transacaoXpModel.getIdRecompensa());
        transacaoXp.setTipo(transacaoXpModel.getTipo());
        transacaoXp.setValor(transacaoXpModel.getValor());
        transacaoXp.setObservacao(transacaoXpModel.getObservacao());
        return transacaoXpRepository.save(transacaoXp);
    }

    public void excluirTransacaoXp(Long idTransacaoXp) {
        transacaoXpRepository.deleteById(idTransacaoXp);
    }
}
