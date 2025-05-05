package random_walk.matcher;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.BaseTest;
import random_walk.automation.api.matcher.service.AvailableTimeMatcherApi;
import random_walk.automation.database.matcher.functions.PersonClubFunctions;
import random_walk.automation.domain.enums.UserRoleEnum;
import ru.random_walk.swagger.matcher_service.model.ClubDto;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.List;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("matcher-e2e")
public class MatcherTest extends BaseTest {

    @Autowired
    protected PersonClubFunctions personClubFunctions;

    @Autowired
    protected AvailableTimeMatcherApi availableTimeMatcherApi;

    private static Boolean isAvailableTimeCalled = false;

    @BeforeAll
    public void addUserAvailableTime() {
        if (!isAvailableTimeCalled) {
            var offsetTime = OffsetTime.now();
            var clubId = personClubFunctions.getUserClubs(userConfigService.getUserByRole(UserRoleEnum.TEST_USER).getUuid())
                    .get(0);
            try {
                availableTimeMatcherApi.addAvailableTime(
                        clubId,
                        offsetTime,
                        OffsetTime.of(23, 59, 0, offsetTime.getNano(), offsetTime.getOffset()),
                        LocalDate.now());
            } catch (Exception ignored) {
            }
            try {
                availableTimeMatcherApi.addAvailableTime(
                        clubId,
                        OffsetTime.of(12, 0, 0, offsetTime.getNano(), offsetTime.getOffset()),
                        OffsetTime.of(13, 0, 0, offsetTime.getNano(), offsetTime.getOffset()),
                        LocalDate.now().plusDays(1));
            } catch (Exception ignored) {
            }
            isAvailableTimeCalled = true;
        }

    }

    protected static List<ClubDto> toListClubDto(List<UUID> personClubs) {
        if (personClubs == null)
            return List.of();
        return personClubs.stream().map(club -> new ClubDto().id(club)).toList();
    }
}
