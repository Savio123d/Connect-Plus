package conne.connect.connect.Assinatura.repository;

import conne.connect.connect.Assinatura.model.AssinaturaModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssinaturaRepository extends JpaRepository<AssinaturaModel, Long> {
    Optional<AssinaturaModel> findByMercadoPagoPreapprovalId(String mercadoPagoPreapprovalId);

    Optional<AssinaturaModel> findByMercadoPagoExternalReference(String mercadoPagoExternalReference);
}