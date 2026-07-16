package conne.connect.connect.Resgate.repository;

import conne.connect.connect.Resgate.model.PedidoResgateModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoResgateRepository extends JpaRepository<PedidoResgateModel, Long> {

    List<PedidoResgateModel> findByExcluidoIsNull();

    List<PedidoResgateModel> findByIdEmpresa_IdEmpresaAndExcluidoIsNull(Long idEmpresa);

    Optional<PedidoResgateModel> findByIdPedidoResgateAndIdEmpresa_IdEmpresaAndExcluidoIsNull(Long idPedidoResgate, Long idEmpresa);
}
