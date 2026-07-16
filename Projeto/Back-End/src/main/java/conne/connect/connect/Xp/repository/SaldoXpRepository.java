package conne.connect.connect.Xp.repository;

import conne.connect.connect.Xp.model.SaldoXpModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaldoXpRepository extends JpaRepository<SaldoXpModel, Long> {

    List<SaldoXpModel> findByExcluidoIsNull();

    List<SaldoXpModel> findByIdEmpresa_IdEmpresaAndExcluidoIsNull(Long idEmpresa);

    Optional<SaldoXpModel> findByIdSaldoXpAndIdEmpresa_IdEmpresaAndExcluidoIsNull(Long idSaldoXp, Long idEmpresa);

    Optional<SaldoXpModel> findByIdUsuarioEmpresa_IdUsuarioEmpresa(Long idUsuarioEmpresa);

    Optional<SaldoXpModel> findByIdUsuarioEmpresa_IdUsuarioEmpresaAndIdEmpresa_IdEmpresa(
            Long idUsuarioEmpresa,
            Long idEmpresa
    );

    Optional<SaldoXpModel> findByIdUsuarioEmpresa_IdUsuarioEmpresaAndExcluidoIsNull(Long idUsuarioEmpresa);

    Optional<SaldoXpModel> findByIdUsuarioEmpresa_IdUsuarioEmpresaAndIdEmpresa_IdEmpresaAndExcluidoIsNull(
            Long idUsuarioEmpresa,
            Long idEmpresa
    );
}
