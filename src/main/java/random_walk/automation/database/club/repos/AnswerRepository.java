package random_walk.automation.database.club.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import random_walk.automation.database.club.entities.Answer;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, UUID> {

    List<Answer> findByApprovementId(UUID approvementId);
}
