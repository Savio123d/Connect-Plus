package conne.connect.connect.Services;

import conne.connect.connect.Models.EquipeMembroModel;
import conne.connect.connect.Repositories.EquipeMembroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipeMembroService {

    @Autowired
    private EquipeMembroRepository equipeMembroRepository;

    public List<EquipeMembroModel> findAll() {
        return equipeMembroRepository.findAll();
    }

    public EquipeMembroModel criarEquipeMembro(EquipeMembroModel equipeMembroModel) {
        return equipeMembroRepository.save(equipeMembroModel);
    }

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
