package conne.connect.connect.Feedback.repository;

import conne.connect.connect.Feedback.model.FeedbackModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackRepository extends JpaRepository<FeedbackModel, Long> {

    @Query("""
            select count(feedback)
            from FeedbackModel feedback
            where feedback.idEmpresa.idEmpresa = :empresaId
              and feedback.excluido is null
            """)
    Long countFeedbacksPorEmpresa(@Param("empresaId") Long empresaId);
}
