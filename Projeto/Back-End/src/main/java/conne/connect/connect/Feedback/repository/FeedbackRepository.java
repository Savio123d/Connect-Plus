package conne.connect.connect.Feedback.repository;

import conne.connect.connect.Feedback.enums.FeedbackClassificacao;
import conne.connect.connect.Feedback.model.FeedbackModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackRepository extends JpaRepository<FeedbackModel, Long> {

    List<FeedbackModel> findByIdEmpresa_IdEmpresaAndExcluidoIsNullOrderByDataCriacaoDesc(Long empresaId);

    List<FeedbackModel> findByIdEmpresa_IdEmpresaAndClassificacaoAndExcluidoIsNullOrderByDataCriacaoDesc(
            Long empresaId,
            FeedbackClassificacao classificacao
    );

    List<FeedbackModel> findByIdEmpresa_IdEmpresaAndAvaliacao360TrueAndExcluidoIsNullOrderByDataCriacaoDesc(
            Long empresaId
    );

    List<FeedbackModel> findByIdEmpresa_IdEmpresaAndIdDestinatarioUsuarioEmpresa_IdUsuarioEmpresaAndExcluidoIsNullOrderByDataCriacaoDesc(
            Long empresaId,
            Long destinatarioId
    );

    List<FeedbackModel> findByIdEmpresa_IdEmpresaAndIdDestinatarioUsuarioEmpresa_IdUsuarioEmpresaAndClassificacaoAndExcluidoIsNullOrderByDataCriacaoDesc(
            Long empresaId,
            Long destinatarioId,
            FeedbackClassificacao classificacao
    );

    List<FeedbackModel> findByIdEmpresa_IdEmpresaAndIdDestinatarioUsuarioEmpresa_IdUsuarioEmpresaAndAvaliacao360IsTrueAndExcluidoIsNullOrderByDataCriacaoDesc(
            Long empresaId,
            Long destinatarioId
    );

    Optional<FeedbackModel> findByIdFeedbackAndIdEmpresa_IdEmpresaAndExcluidoIsNull(
            Long idFeedback,
            Long empresaId
    );

    Long countByIdEmpresa_IdEmpresaAndClassificacaoAndExcluidoIsNull(
            Long empresaId,
            FeedbackClassificacao classificacao
    );

    boolean existsByIdEmpresa_IdEmpresaAndIdProjeto_IdProjetoAndIdAutorUsuarioEmpresa_IdUsuarioEmpresaAndIdDestinatarioUsuarioEmpresa_IdUsuarioEmpresaAndAvaliacao360TrueAndExcluidoIsNull(
            Long empresaId,
            Long projetoId,
            Long autorUsuarioEmpresaId,
            Long destinatarioUsuarioEmpresaId
    );

    @Query("""
            select count(feedback)
            from FeedbackModel feedback
            where feedback.idEmpresa.idEmpresa = :empresaId
              and feedback.excluido is null
            """)
    Long countFeedbacksPorEmpresa(@Param("empresaId") Long empresaId);
}
