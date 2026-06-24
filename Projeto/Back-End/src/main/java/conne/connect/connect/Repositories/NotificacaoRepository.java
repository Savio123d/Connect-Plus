package conne.connect.connect.Repositories;

import conne.connect.connect.Models.NotificacaoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<NotificacaoModel, Long> {
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
