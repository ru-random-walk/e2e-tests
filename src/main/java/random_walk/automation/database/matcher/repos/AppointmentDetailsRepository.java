package random_walk.automation.database.matcher.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import random_walk.automation.database.matcher.entities.AppointmentDetails;

import java.util.List;
import java.util.UUID;

public interface AppointmentDetailsRepository extends JpaRepository<AppointmentDetails, UUID> {

    @Query(nativeQuery = true, value = "SELECT * FROM appointment_details WHERE requester_id = ?1")
    List<AppointmentDetails> findByRequesterId(UUID requesterId);
}
