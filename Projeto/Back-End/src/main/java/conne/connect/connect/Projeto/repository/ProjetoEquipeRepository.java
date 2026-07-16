package conne.connect.connect.Projeto.repository;

import conne.connect.connect.Projeto.model.ProjetoEquipeModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjetoEquipeRepository extends JpaRepository<ProjetoEquipeModel, Long> {

    List<ProjetoEquipeModel> findByExcluidoIsNull();

    List<ProjetoEquipeModel> findByIdEmpresa_IdEmpresaAndExcluidoIsNull(Long idEmpresa);

    Optional<ProjetoEquipeModel> findByIdProjetoEquipeAndIdEmpresa_IdEmpresaAndExcluidoIsNull(Long idProjetoEquipe, Long idEmpresa);
}
