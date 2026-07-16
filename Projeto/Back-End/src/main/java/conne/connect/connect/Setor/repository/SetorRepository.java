package conne.connect.connect.Setor.repository;

import conne.connect.connect.Setor.model.SetorModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SetorRepository extends JpaRepository<SetorModel, Long> {

    List<SetorModel> findByExcluidoIsNull();

    List<SetorModel> findByIdEmpresa_IdEmpresaAndExcluidoIsNull(Long idEmpresa);

    Optional<SetorModel> findByIdSetorAndIdEmpresa_IdEmpresaAndExcluidoIsNull(Long idSetor, Long idEmpresa);
}
