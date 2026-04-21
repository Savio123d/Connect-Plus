package conne.connect.connect.Services;

import conne.connect.connect.Models.ProjetoModel;
import conne.connect.connect.Repositories.ProjetoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjetoService {

    @Autowired
    private ProjetoRepository projetoRepository;

    public List<ProjetoModel> findAll() {
        return projetoRepository.findAll();
    }

    public ProjetoModel criarProjeto(ProjetoModel projetoModel) {
        return projetoRepository.save(projetoModel);
    }

    public Optional<ProjetoModel> buscarPorId(Long idProjeto) {
        return projetoRepository.findById(idProjeto);
    }

    public ProjetoModel atualizarProjeto(Long idProjeto, ProjetoModel projetoModel) {
        ProjetoModel projeto = projetoRepository.findById(idProjeto).get();
        projeto.setIdEmpresa(projetoModel.getIdEmpresa());
        projeto.setIdGestorUsuarioEmpresa(projetoModel.getIdGestorUsuarioEmpresa());
        projeto.setNome(projetoModel.getNome());
        projeto.setDescricao(projetoModel.getDescricao());
        projeto.setDataInicio(projetoModel.getDataInicio());
        projeto.setDataFimPrevista(projetoModel.getDataFimPrevista());
        projeto.setDataFimReal(projetoModel.getDataFimReal());
        projeto.setStatus(projetoModel.getStatus());
        return projetoRepository.save(projeto);
    }

    public void excluirProjeto(Long idProjeto) {
        projetoRepository.deleteById(idProjeto);
    }
}
