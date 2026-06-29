package conne.connect.connect.Resgate.repository;

import conne.connect.connect.Resgate.model.ResgateRecompensaModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResgateRecompensaRepository extends JpaRepository<ResgateRecompensaModel, Long> {

    boolean existsByIdUsuarioEmpresa_IdUsuarioEmpresaAndIdRecompensa_IdRecompensaAndStatusAndExcluidoIsNull(
            Long idUsuarioEmpresa,
            Long idRecompensa,
            String status
    );
}
