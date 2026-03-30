package conne.connect.connect.Services;

import conne.connect.connect.Models.SetorModel;
import conne.connect.connect.Repositories.SetorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SetorService {

    @Autowired
    private SetorRepository setorRepository;

    public List<SetorModel> findAll() {
        return setorRepository.findAll();
    }

    public SetorModel criarSetor(SetorModel setorModel) {
        return setorRepository.save(setorModel);
    }

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
