package conne.connect.connect.Resgate.repository;

import conne.connect.connect.Resgate.model.ResgateRecompensaModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResgateRecompensaRepository extends JpaRepository<ResgateRecompensaModel, Long> {

    List<ResgateRecompensaModel> findByExcluidoIsNull();

    List<ResgateRecompensaModel> findByIdEmpresa_IdEmpresaAndExcluidoIsNull(Long idEmpresa);

    Optional<ResgateRecompensaModel> findByIdResgateRecompensaAndIdEmpresa_IdEmpresaAndExcluidoIsNull(Long idResgateRecompensa, Long idEmpresa);

    boolean existsByIdUsuarioEmpresa_IdUsuarioEmpresaAndIdRecompensa_IdRecompensaAndStatusAndExcluidoIsNull(
            Long idUsuarioEmpresa,
            Long idRecompensa,
            String status
    );
}
