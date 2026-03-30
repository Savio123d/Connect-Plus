package conne.connect.connect.Services;

import conne.connect.connect.Models.ProjetoEquipeModel;
import conne.connect.connect.Repositories.ProjetoEquipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjetoEquipeService {

    @Autowired
    private ProjetoEquipeRepository projetoEquipeRepository;

    public List<ProjetoEquipeModel> findAll() {
        return projetoEquipeRepository.findAll();
    }

    public ProjetoEquipeModel criarProjetoEquipe(ProjetoEquipeModel projetoEquipeModel) {
        return projetoEquipeRepository.save(projetoEquipeModel);
    }

    public Optional<ProjetoEquipeModel> buscarPorId(Long idProjetoEquipe) {
        return projetoEquipeRepository.findById(idProjetoEquipe);
    }

    public ProjetoEquipeModel atualizarProjetoEquipe(Long idProjetoEquipe, ProjetoEquipeModel projetoEquipeModel) {
        ProjetoEquipeModel projetoEquipe = projetoEquipeRepository.findById(idProjetoEquipe).get();
        projetoEquipe.setIdEmpresa(projetoEquipeModel.getIdEmpresa());
        projetoEquipe.setIdProjeto(projetoEquipeModel.getIdProjeto());
        projetoEquipe.setIdEquipe(projetoEquipeModel.getIdEquipe());
        return projetoEquipeRepository.save(projetoEquipe);
    }

    public void excluirProjetoEquipe(Long idProjetoEquipe) {
        projetoEquipeRepository.deleteById(idProjetoEquipe);
    }
}
