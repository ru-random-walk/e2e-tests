package random_walk.automation.database.matcher.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.database.matcher.entities.AvailableTime;
import random_walk.automation.database.matcher.repos.AvailableTimeRepository;
import random_walk.automation.util.PointConverterUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvailableTimeFunctions {

    private final AvailableTimeRepository availableTimeRepository;

    public List<AvailableTime> getUserAvailableTime(UUID personId) {
        return availableTimeRepository.findByPersonId(personId);
    }

    public AvailableTime getById(UUID id) {
        return availableTimeRepository.findById(id).orElse(null);
    }

    public List<AvailableTime> getUserAvailableTimeByDateAndPersonId(LocalDate date, UUID personId) {
        return availableTimeRepository.findByDateAndPersonId(date, personId)
                .stream()
                .map(r -> r.setLocation(PointConverterUtils.convertToPoint(r.getLocation()).toString()))
                .toList();
    }

    public List<AvailableTime> getAllAvailableTime() {
        return availableTimeRepository.findAll();
    }
}
