package conne.connect.connect.Services;

import conne.connect.connect.Models.RecompensaModel;
import conne.connect.connect.Repositories.RecompensaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecompensaService {

    @Autowired
    private RecompensaRepository recompensaRepository;

    public List<RecompensaModel> findAll() {
        return recompensaRepository.findAll();
    }

    public RecompensaModel criarRecompensa(RecompensaModel recompensaModel) {
        return recompensaRepository.save(recompensaModel);
    }

    public Optional<RecompensaModel> buscarPorId(Long idRecompensa) {
        return recompensaRepository.findById(idRecompensa);
    }

    public RecompensaModel atualizarRecompensa(Long idRecompensa, RecompensaModel recompensaModel) {
        RecompensaModel recompensa = recompensaRepository.findById(idRecompensa).get();
        recompensa.setIdEmpresa(recompensaModel.getIdEmpresa());
        recompensa.setNome(recompensaModel.getNome());
        recompensa.setDescricao(recompensaModel.getDescricao());
        recompensa.setXpNecessario(recompensaModel.getXpNecessario());
        recompensa.setAtiva(recompensaModel.getAtiva());
        return recompensaRepository.save(recompensa);
    }

    public void excluirRecompensa(Long idRecompensa) {
        recompensaRepository.deleteById(idRecompensa);
    }
}
