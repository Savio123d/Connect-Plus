package conne.connect.connect.Equipe.service;

import conne.connect.connect.Equipe.model.EquipeMembroModel;
import conne.connect.connect.Equipe.repository.EquipeMembroRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EquipeMembroService {

    @Autowired
    private EquipeMembroRepository equipeMembroRepository;

    @Transactional(readOnly = true)
    public List<EquipeMembroModel> findAll() {
        return equipeMembroRepository.findAll();
    }

    public EquipeMembroModel criarEquipeMembro(EquipeMembroModel equipeMembroModel) {
        return equipeMembroRepository.save(equipeMembroModel);
    }

    @Transactional(readOnly = true)
    public Optional<EquipeMembroModel> buscarPorId(Long idEquipeMembro) {
        return equipeMembroRepository.findById(idEquipeMembro);
    }

    public EquipeMembroModel atualizarEquipeMembro(Long idEquipeMembro, EquipeMembroModel equipeMembroModel) {
        EquipeMembroModel equipeMembro = equipeMembroRepository.findById(idEquipeMembro).get();
        equipeMembro.setIdEmpresa(equipeMembroModel.getIdEmpresa());
        equipeMembro.setIdEquipe(equipeMembroModel.getIdEquipe());
        equipeMembro.setIdUsuarioEmpresa(equipeMembroModel.getIdUsuarioEmpresa());
        return equipeMembroRepository.save(equipeMembro);
    }

    public void excluirEquipeMembro(Long idEquipeMembro) {
        equipeMembroRepository.deleteById(idEquipeMembro);
    }
}
