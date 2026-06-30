package conne.connect.connect.Setor.service;

import conne.connect.connect.Setor.model.SetorModel;
import conne.connect.connect.Setor.repository.SetorRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SetorService {

    @Autowired
    private SetorRepository setorRepository;

    @Transactional(readOnly = true)
    public List<SetorModel> findAll() {
        return setorRepository.findAll();
    }

    public SetorModel criarSetor(SetorModel setorModel) {
        return setorRepository.save(setorModel);
    }

    @Transactional(readOnly = true)
    public Optional<SetorModel> buscarPorId(Long idSetor) {
        return setorRepository.findById(idSetor);
    }

    public SetorModel atualizarSetor(Long idSetor, SetorModel setorModel) {
        SetorModel setor = setorRepository.findById(idSetor).get();
        setor.setIdEmpresa(setorModel.getIdEmpresa());
        setor.setNome(setorModel.getNome());
        setor.setDescricao(setorModel.getDescricao());
        setor.setAtivo(setorModel.getAtivo());
        return setorRepository.save(setor);
    }

    public void excluirSetor(Long idSetor) {
        setorRepository.deleteById(idSetor);
    }
}
