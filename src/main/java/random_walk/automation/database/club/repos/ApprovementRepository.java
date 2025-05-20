package random_walk.automation.database.club.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import random_walk.automation.database.club.entities.Approvement;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApprovementRepository extends JpaRepository<Approvement, UUID> {

    List<Approvement> findByClubId(UUID clubId);
}
