package random_walk.automation.database.matcher.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import random_walk.automation.database.matcher.entities.AvailableTime;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AvailableTimeRepository extends JpaRepository<AvailableTime, UUID> {

    List<AvailableTime> findByPersonId(UUID personId);

    List<AvailableTime> findByDateAndPersonId(LocalDate date, UUID personId);

    @Transactional(transactionManager = "matcherTransactionManager")
    @Modifying
    void deleteByPersonId(UUID personId);
}
