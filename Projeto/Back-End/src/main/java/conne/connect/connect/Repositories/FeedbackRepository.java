package conne.connect.connect.Repositories;

import conne.connect.connect.Models.FeedbackModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackRepository extends JpaRepository<FeedbackModel, Long> {

    @Query(value = """
        SELECT COUNT(*)
        FROM feedback
        WHERE empresa_id = :empresaId
    """, nativeQuery = true)
    Long countFeedbacksPorEmpresa(@Param("empresaId") Long empresaId);
}