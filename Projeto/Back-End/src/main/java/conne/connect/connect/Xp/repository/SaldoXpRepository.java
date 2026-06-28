package conne.connect.connect.Xp.repository;

import conne.connect.connect.Xp.model.SaldoXpModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaldoXpRepository extends JpaRepository<SaldoXpModel, Long> {

    Optional<SaldoXpModel> findByIdUsuarioEmpresa_IdUsuarioEmpresa(Long idUsuarioEmpresa);
}
