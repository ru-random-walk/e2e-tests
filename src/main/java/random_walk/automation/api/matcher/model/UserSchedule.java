package random_walk.automation.api.matcher.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.random_walk.swagger.matcher_service.model.LocationDto;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class UserSchedule {

    private String date;
    private String timezone;
    private int walkCount;
    private List<TimeFrame> timeFrames;

    @Data
    public static class TimeFrame {
        private UUID partnerId;
        private UUID requesterId;
        private UUID appointmentId;
        private UUID availableTimeId;
        private String timeFrom;
        private String timeUntil;
        private LocationDto location;
        private List<UUID> availableTimeClubsInFilter;
        private String appointmentStatus;

    }
}
