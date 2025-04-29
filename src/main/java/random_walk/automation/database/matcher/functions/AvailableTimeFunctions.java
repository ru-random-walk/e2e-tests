package random_walk.automation.database.matcher.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.database.matcher.entities.AvailableTime;
import random_walk.automation.database.matcher.repos.AvailableTimeRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvailableTimeFunctions {

    private final AvailableTimeRepository availableTimeRepository;

    public List<AvailableTime> getUserAvailableTime(UUID personId) {
        return availableTimeRepository.findByPersonId(personId);
    }
}
