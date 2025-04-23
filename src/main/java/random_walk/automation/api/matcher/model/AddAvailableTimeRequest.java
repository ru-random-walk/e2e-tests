package random_walk.automation.api.matcher.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import ru.random_walk.swagger.matcher_service.model.LocationDto;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class AddAvailableTimeRequest {

    private LocalDate date;

    private OffsetTime timeFrom;

    private OffsetTime timeUntil;

    private LocationDto location;

    private List<UUID> clubsInFilter;
}
