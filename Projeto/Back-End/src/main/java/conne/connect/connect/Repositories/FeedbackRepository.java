package conne.connect.connect.Repositories;

import conne.connect.connect.Models.FeedbackModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<FeedbackModel, Long> {
}
