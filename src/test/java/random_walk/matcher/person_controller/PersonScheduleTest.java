package random_walk.matcher.person_controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.matcher.model.UserSchedule;
import random_walk.automation.api.matcher.service.AvailableTimeMatcherApi;
import random_walk.automation.api.matcher.service.PersonMatcherApi;
import random_walk.automation.database.matcher.entities.AvailableTime;
import random_walk.automation.database.matcher.functions.AvailableTimeFunctions;
import random_walk.automation.database.matcher.functions.PersonClubFunctions;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.matcher.MatcherTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersonScheduleTest extends MatcherTest {

    @Autowired
    private PersonMatcherApi personMatcherApi;

    @Autowired
    private AvailableTimeMatcherApi availableTimeMatcherApi;

    @Autowired
    private PersonClubFunctions personClubFunctions;

    @Autowired
    private AvailableTimeFunctions availableTimeFunctions;

    /*
     * @BeforeAll public void addUserAvailableTime() { var clubId =
     * personClubFunctions.getUserClubs(userConfigService.getUserByRole(UserRoleEnum
     * .TEST_USER).getUuid()).get(0); try {
     * availableTimeMatcherApi.addAvailableTime(clubId, OffsetTime.now(),
     * OffsetTime.now().plusHours(1), LocalDate.now()); } catch (Exception ignored)
     * {} try { availableTimeMatcherApi.addAvailableTime(clubId,
     * OffsetTime.now().plusHours(2), OffsetTime.now().plusHours(4),
     * LocalDate.now()); } catch (Exception ignored) {} try {
     * availableTimeMatcherApi.addAvailableTime(clubId,
     * OffsetTime.now().plusHours(1), OffsetTime.now().plusHours(2),
     * LocalDate.now().plusDays(1)); } catch (Exception ignored) {} }
     */

    @Test
    @DisplayName("Проверка получения заданного времени для поиска прогулок")
    void getPersonScheduleTest() {
        var testUserInfo = userConfigService.getUserByRole(UserRoleEnum.TEST_USER);

        var personSchedule = Arrays.stream(personMatcherApi.getInfoAboutSchedule()).toList();
    }

    private void checkAvailableTime(List<UserSchedule> givenList, List<AvailableTime> databaseList) {
        List<UserSchedule> expectedList = new ArrayList<>();
        /*
         * databaseList.forEach( databaseSchedule -> expectedList.add(new
         * UserSchedule().set) );
         */
    }
}
