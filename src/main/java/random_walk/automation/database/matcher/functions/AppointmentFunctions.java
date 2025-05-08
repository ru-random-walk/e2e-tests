package random_walk.automation.database.matcher.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.database.matcher.entities.Appointment;
import random_walk.automation.database.matcher.repos.AppointmentRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentFunctions {

    private final AppointmentRepository appointmentRepository;

    public void deleteByAppointmentId(UUID appointmentId) {
        appointmentRepository.deleteByAppointmentId(appointmentId);
    }

    public List<UUID> getAppointmentParticipants(UUID appointmentId) {
        return appointmentRepository.findByAppointmentId(appointmentId).stream().map(Appointment::getPersonId).toList();
    }

    public List<Appointment> getAppointmentsByPersonId(UUID personId) {
        return appointmentRepository.findAllByPersonId(personId);
    }

    public List<UUID> getUsersAppointment(UUID firstUser, UUID secondUser) {
        var firstUserAppointments = getAppointmentsByPersonId(firstUser).stream().map(Appointment::getAppointmentId).toList();
        var secondUserAppointments = getAppointmentsByPersonId(secondUser).stream().map(Appointment::getAppointmentId).toList();
        return firstUserAppointments.stream().filter(secondUserAppointments::contains).toList();
    }
}
