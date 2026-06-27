package conne.connect.connect.Equipe.service;

import conne.connect.connect.Equipe.model.EquipeModel;
import conne.connect.connect.Equipe.repository.EquipeRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EquipeService {

    @Autowired
    private EquipeRepository equipeRepository;

    public List<EquipeModel> findAll() {
        return equipeRepository.findAll();
    }

    public EquipeModel criarEquipe(EquipeModel equipeModel) {
        return equipeRepository.save(equipeModel);
    }

    public Optional<EquipeModel> buscarPorId(Long idEquipe) {
        return equipeRepository.findById(idEquipe);
    }

    public EquipeModel atualizarEquipe(Long idEquipe, EquipeModel equipeModel) {
        EquipeModel equipe = equipeRepository.findById(idEquipe).get();
        equipe.setIdEmpresa(equipeModel.getIdEmpresa());
        equipe.setNome(equipeModel.getNome());
        equipe.setDescricao(equipeModel.getDescricao());
        equipe.setAtivo(equipeModel.getAtivo());
        return equipeRepository.save(equipe);
    }

    public void excluirEquipe(Long idEquipe) {
        equipeRepository.deleteById(idEquipe);
    }
}
