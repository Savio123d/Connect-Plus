package conne.connect.connect.Xp.repository;

import conne.connect.connect.Xp.enums.TipoTransacaoXp;
import conne.connect.connect.Xp.model.TransacaoXpModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacaoXpRepository extends JpaRepository<TransacaoXpModel, Long> {

    List<TransacaoXpModel> findByExcluidoIsNull();

    List<TransacaoXpModel> findByIdEmpresa_IdEmpresaAndExcluidoIsNull(Long idEmpresa);

    Optional<TransacaoXpModel> findByIdTransacaoXpAndIdEmpresa_IdEmpresaAndExcluidoIsNull(Long idTransacaoXp, Long idEmpresa);

    List<TransacaoXpModel> findTop50ByIdUsuarioEmpresa_IdUsuarioEmpresaOrderByDataCriacaoDesc(Long idUsuarioEmpresa);

    Optional<TransacaoXpModel> findFirstByIdTarefa_IdTarefaAndIdEmpresa_IdEmpresaAndIdUsuarioEmpresa_IdUsuarioEmpresaAndTipoAndExcluidoIsNull(
            Long idTarefa,
            Long idEmpresa,
            Long idUsuarioEmpresa,
            TipoTransacaoXp tipo
    );
}
