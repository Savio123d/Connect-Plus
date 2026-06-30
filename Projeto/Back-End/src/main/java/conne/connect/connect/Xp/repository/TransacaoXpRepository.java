package conne.connect.connect.Xp.repository;

import conne.connect.connect.Xp.model.TransacaoXpModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacaoXpRepository extends JpaRepository<TransacaoXpModel, Long> {

    List<TransacaoXpModel> findTop50ByIdUsuarioEmpresa_IdUsuarioEmpresaOrderByDataCriacaoDesc(Long idUsuarioEmpresa);
}
