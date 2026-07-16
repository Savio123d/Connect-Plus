package conne.connect.connect.Resgate.repository;

import conne.connect.connect.Resgate.model.PedidoResgateItemModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoResgateItemRepository extends JpaRepository<PedidoResgateItemModel, Long> {

    List<PedidoResgateItemModel> findByExcluidoIsNull();

    List<PedidoResgateItemModel> findByIdPedidoResgate_IdEmpresa_IdEmpresaAndExcluidoIsNull(Long idEmpresa);

    Optional<PedidoResgateItemModel> findByIdPedidoResgateItemAndIdPedidoResgate_IdEmpresa_IdEmpresaAndExcluidoIsNull(Long idPedidoResgateItem, Long idEmpresa);
}
