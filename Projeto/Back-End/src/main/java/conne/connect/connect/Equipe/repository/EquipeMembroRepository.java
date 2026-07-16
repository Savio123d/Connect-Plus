package conne.connect.connect.Equipe.repository;

import conne.connect.connect.Equipe.model.EquipeMembroModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipeMembroRepository extends JpaRepository<EquipeMembroModel, Long> {

    List<EquipeMembroModel> findByExcluidoIsNull();

    List<EquipeMembroModel> findByIdEmpresa_IdEmpresaAndExcluidoIsNull(Long idEmpresa);

    Optional<EquipeMembroModel> findByIdEquipeMembroAndIdEmpresa_IdEmpresaAndExcluidoIsNull(Long idEquipeMembro, Long idEmpresa);
}
