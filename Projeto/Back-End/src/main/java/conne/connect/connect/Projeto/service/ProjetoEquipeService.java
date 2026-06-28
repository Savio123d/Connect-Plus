package conne.connect.connect.Projeto.service;

import conne.connect.connect.Projeto.model.ProjetoEquipeModel;
import conne.connect.connect.Projeto.repository.ProjetoEquipeRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
