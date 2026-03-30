package conne.connect.connect.Services;

import conne.connect.connect.Models.ResgateRecompensaModel;
import conne.connect.connect.Repositories.ResgateRecompensaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResgateRecompensaService {

    @Autowired
    private ResgateRecompensaRepository resgateRecompensaRepository;

    public List<ResgateRecompensaModel> findAll() {
        return resgateRecompensaRepository.findAll();
    }

    public ResgateRecompensaModel criarResgateRecompensa(ResgateRecompensaModel resgateRecompensaModel) {
        return resgateRecompensaRepository.save(resgateRecompensaModel);
    }

    public Optional<ResgateRecompensaModel> buscarPorId(Long idResgateRecompensa) {
        return resgateRecompensaRepository.findById(idResgateRecompensa);
    }

    public ResgateRecompensaModel atualizarResgateRecompensa(Long idResgateRecompensa, ResgateRecompensaModel resgateRecompensaModel) {
        ResgateRecompensaModel resgateRecompensa = resgateRecompensaRepository.findById(idResgateRecompensa).get();
        resgateRecompensa.setIdEmpresa(resgateRecompensaModel.getIdEmpresa());
        resgateRecompensa.setIdUsuarioEmpresa(resgateRecompensaModel.getIdUsuarioEmpresa());
        resgateRecompensa.setIdRecompensa(resgateRecompensaModel.getIdRecompensa());
        resgateRecompensa.setIdTransacaoXp(resgateRecompensaModel.getIdTransacaoXp());
        resgateRecompensa.setQuantidade(resgateRecompensaModel.getQuantidade());
        resgateRecompensa.setXpGasto(resgateRecompensaModel.getXpGasto());
        resgateRecompensa.setStatus(resgateRecompensaModel.getStatus());
        return resgateRecompensaRepository.save(resgateRecompensa);
    }

    public void excluirResgateRecompensa(Long idResgateRecompensa) {
        resgateRecompensaRepository.deleteById(idResgateRecompensa);
    }
}
