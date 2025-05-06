package random_walk.automation.database.matcher.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import random_walk.automation.database.matcher.entities.Appointment;
import random_walk.automation.database.matcher.entities.prkeys.AppointmentPK;

import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, AppointmentPK> {

    @Transactional(transactionManager = "matcherTransactionManager")
    @Modifying
    void deleteByAppointmentId(UUID appointmentId);

    List<Appointment> findByAppointmentId(UUID appointmentId);
}
