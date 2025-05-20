package random_walk.automation.database.matcher.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.database.matcher.entities.DayLimit;
import random_walk.automation.database.matcher.entities.prkeys.DayLimitPK;
import random_walk.automation.database.matcher.repos.DayLimitRepository;

@Service
@RequiredArgsConstructor
public class DayLimitFunctions {

    private final DayLimitRepository dayLimitRepository;

    public DayLimit getById(DayLimitPK dayLimitPK) {
        return dayLimitRepository.findById(dayLimitPK).orElse(null);
    }

    public void setDayLimitByDateAndPersonId(DayLimitPK dayLimitPK) {
        dayLimitRepository.setDayLimitById(dayLimitPK);
    }
}
