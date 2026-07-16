package conne.connect.connect.Recompensa.repository;

import conne.connect.connect.Recompensa.model.RecompensaModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecompensaRepository extends JpaRepository<RecompensaModel, Long> {

    List<RecompensaModel> findByExcluidoIsNull();

    List<RecompensaModel> findByIdEmpresa_IdEmpresaAndExcluidoIsNullOrderByNomeAsc(Long idEmpresa);

    List<RecompensaModel> findByIdEmpresa_IdEmpresaAndAtivaTrueAndExcluidoIsNullOrderByNomeAsc(Long idEmpresa);

    Optional<RecompensaModel> findByIdRecompensaAndIdEmpresa_IdEmpresaAndExcluidoIsNull(Long idRecompensa, Long idEmpresa);
}
