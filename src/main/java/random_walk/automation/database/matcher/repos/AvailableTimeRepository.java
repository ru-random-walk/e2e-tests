package random_walk.automation.database.matcher.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import random_walk.automation.database.matcher.entities.AvailableTime;

import java.util.List;
import java.util.UUID;

public interface AvailableTimeRepository extends JpaRepository<AvailableTime, UUID> {

    List<AvailableTime> findByPersonId(UUID personId);
}
