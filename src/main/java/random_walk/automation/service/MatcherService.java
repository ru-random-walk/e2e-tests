package random_walk.automation.service;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.NotFoundException;
import org.springframework.stereotype.Service;
import random_walk.automation.database.matcher.functions.AppointmentDetailsFunctions;
import random_walk.automation.database.matcher.functions.AppointmentFunctions;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatcherService {

    private final AppointmentFunctions appointmentFunctions;

    private final AppointmentDetailsFunctions appointmentDetailsFunctions;

    public void deleteAppointmentRequest(UUID appointmentId) {
        appointmentFunctions.deleteByAppointmentId(appointmentId);
        appointmentDetailsFunctions.deleteById(appointmentId);
    }

    public void deleteAppointmentRequest(UUID firstUserId, UUID secondUserId) {
        var appointmentIds = appointmentFunctions.getUsersAppointment(firstUserId, secondUserId);
        if (!appointmentIds.isEmpty()) {
            appointmentIds.forEach(appointmentId -> {
                appointmentFunctions.deleteByAppointmentId(appointmentId);
                appointmentDetailsFunctions.deleteById(appointmentId);
            });
        } else {
            throw new NotFoundException(
                    "Встреч для пользователей %s и %s не существует в базе".formatted(firstUserId, secondUserId));
        }
    }
}
