package conne.connect.connect.Projeto.service;

import conne.connect.connect.Projeto.model.ProjetoModel;
import conne.connect.connect.Projeto.repository.ProjetoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjetoService {

    @Autowired
    private ProjetoRepository projetoRepository;

    @Transactional(readOnly = true)
    public List<ProjetoModel> findAll() {
        return projetoRepository.findAll();
    }

    public ProjetoModel criarProjeto(ProjetoModel projetoModel) {
        return projetoRepository.save(projetoModel);
    }

    @Transactional(readOnly = true)
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
