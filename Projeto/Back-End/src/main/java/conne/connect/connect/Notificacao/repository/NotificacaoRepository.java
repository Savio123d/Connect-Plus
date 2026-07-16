package conne.connect.connect.Notificacao.repository;

import conne.connect.connect.Notificacao.model.NotificacaoModel;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacaoRepository extends JpaRepository<NotificacaoModel, Long> {
    List<NotificacaoModel> findByIdEmpresa_IdEmpresaAndExcluidoIsNull(Long idEmpresa);

    Optional<NotificacaoModel> findByIdNotificacaoAndExcluidoIsNull(Long idNotificacao);

    List<NotificacaoModel> findByIdUsuarioEmpresa_IdUsuarioEmpresaAndExcluidoIsNullOrderByDataCriacaoDesc(
            Long idUsuarioEmpresa
    );

    List<NotificacaoModel> findTop5ByIdUsuarioEmpresa_IdUsuarioEmpresaAndExcluidoIsNullOrderByDataCriacaoDesc(
            Long idUsuarioEmpresa
    );

    long countByIdUsuarioEmpresa_IdUsuarioEmpresaAndLidaFalseAndExcluidoIsNull(
            Long idUsuarioEmpresa
    );
}
