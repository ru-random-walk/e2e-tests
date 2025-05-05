package random_walk.automation.database.matcher.functions;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.NotFoundException;
import org.springframework.stereotype.Service;
import random_walk.automation.database.matcher.entities.AppointmentDetails;
import random_walk.automation.database.matcher.repos.AppointmentDetailsRepository;
import random_walk.automation.util.PointConverterUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentDetailsFunctions {

    private final AppointmentDetailsRepository appointmentDetailsRepository;

    public AppointmentDetails getById(UUID id) {
        var appointmentDetails = appointmentDetailsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдена прогулка с id = " + id));
        return appointmentDetails.setApproximateLocation(
                PointConverterUtils.convertToPoint(appointmentDetails.getApproximateLocation()).toString());
    }

    public List<AppointmentDetails> getByRequesterId(UUID requesterId) {
        return appointmentDetailsRepository.findByRequesterId(requesterId)
                .stream()
                .map(r -> r.setApproximateLocation(PointConverterUtils.convertToPoint(r.getApproximateLocation()).toString()))
                .toList();
    }
}
