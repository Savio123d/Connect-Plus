package conne.connect.connect.Equipe.repository;

import conne.connect.connect.Equipe.model.EquipeModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipeRepository extends JpaRepository<EquipeModel, Long> {

    List<EquipeModel> findByExcluidoIsNull();

    List<EquipeModel> findByIdEmpresa_IdEmpresaAndExcluidoIsNull(Long idEmpresa);

    Optional<EquipeModel> findByIdEquipeAndIdEmpresa_IdEmpresaAndExcluidoIsNull(Long idEquipe, Long idEmpresa);
}
