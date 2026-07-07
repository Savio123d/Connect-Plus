package conne.connect.connect.Plano.repository;

import conne.connect.connect.Plano.enums.TipoPlano;
import conne.connect.connect.Plano.model.PlanoModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanoRepository extends JpaRepository<PlanoModel, Long> {
    Optional<PlanoModel> findFirstByTipoAndExcluidoIsNull(TipoPlano tipo);
}