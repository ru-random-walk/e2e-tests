package random_walk.matcher;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.BaseTest;
import random_walk.automation.api.club.services.MemberControllerApi;
import random_walk.automation.api.matcher.service.AvailableTimeMatcherApi;
import random_walk.automation.domain.enums.ClubRole;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.automation.service.MatcherService;
import ru.random_walk.swagger.matcher_service.model.ClubDto;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.List;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("matcher-e2e")
public class MatcherTest extends BaseTest {

    @Autowired
    protected AvailableTimeMatcherApi availableTimeMatcherApi;

    @Autowired
    protected MatcherService matcherService;

    @Autowired
    private MemberControllerApi memberControllerApi;

    private static Boolean isAvailableTimeCalled = false;

    protected static final Double LATITUDE = 56.304017;
    protected static final Double LONGITUDE = 43.982207;

    @Step
    @Title("Добавление свободного времени пользователю TEST_USER и FIRST_TEST_USER")
    @BeforeAll
    public void addUserAvailableTime() {
        if (!isAvailableTimeCalled) {
            var offsetTime = OffsetTime.now();
            var clubId = clubConfigService.getClubByRole(ClubRole.DEFAULT_CLUB).getId();
            try {
                availableTimeMatcherApi.addAvailableTime(
                        testTokenConfig.getToken(),
                        clubId,
                        offsetTime,
                        OffsetTime.of(23, 59, 0, offsetTime.getNano(), offsetTime.getOffset()),
                        LocalDate.now(),
                        LATITUDE,
                        LONGITUDE);
            } catch (Exception ignored) {
            }
            try {
                availableTimeMatcherApi.addAvailableTime(
                        testTokenConfig.getToken(),
                        clubId,
                        OffsetTime.of(12, 0, 0, offsetTime.getNano(), offsetTime.getOffset()),
                        OffsetTime.of(13, 0, 0, offsetTime.getNano(), offsetTime.getOffset()),
                        LocalDate.now().plusDays(1),
                        LATITUDE,
                        LONGITUDE);
            } catch (Exception ignored) {
            }
            try {
                availableTimeMatcherApi.addAvailableTime(
                        userConfigService.getUserByRole(UserRoleEnum.FIRST_TEST_USER).getAccessToken(),
                        clubId,
                        OffsetTime.of(12, 0, 0, offsetTime.getNano(), offsetTime.getOffset()),
                        OffsetTime.of(23, 0, 0, offsetTime.getNano(), offsetTime.getOffset()),
                        LocalDate.now().plusDays(1),
                        LATITUDE,
                        LONGITUDE);
            } catch (Exception ignored) {
            }
            try {
                availableTimeMatcherApi.addAvailableTime(
                        userConfigService.getUserByRole(UserRoleEnum.FIRST_TEST_USER).getAccessToken(),
                        clubId,
                        OffsetTime.of(15, 0, 0, offsetTime.getNano(), offsetTime.getOffset()),
                        OffsetTime.of(17, 0, 0, offsetTime.getNano(), offsetTime.getOffset()),
                        LocalDate.now().plusDays(2),
                        LATITUDE,
                        LONGITUDE);
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
