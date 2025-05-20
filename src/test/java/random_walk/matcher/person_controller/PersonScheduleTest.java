package random_walk.matcher.person_controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.matcher.model.UserSchedule;
import random_walk.automation.api.matcher.service.PersonMatcherApi;
import random_walk.automation.database.matcher.entities.AvailableTime;
import random_walk.automation.database.matcher.entities.prkeys.DayLimitPK;
import random_walk.automation.database.matcher.functions.AvailableTimeFunctions;
import random_walk.automation.database.matcher.functions.DayLimitFunctions;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.matcher.MatcherTest;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

class PersonScheduleTest extends MatcherTest {

    @Autowired
    private PersonMatcherApi personMatcherApi;

    @Autowired
    private AvailableTimeFunctions availableTimeFunctions;

    @Autowired
    private DayLimitFunctions dayLimitFunctions;

    @Test
    @DisplayName("Проверка получения заданного времени для поиска прогулок")
    void getPersonScheduleTest() {
        var testUserInfo = userConfigService.getUserByRole(UserRoleEnum.TEST_USER);

        var personSchedule = Arrays.stream(personMatcherApi.getInfoAboutSchedule(testTokenConfig.getToken())).toList();

        personSchedule.forEach(availableTime -> {
            var timeFrames = availableTimeFunctions
                    .getUserAvailableTimeByDateAndPersonId(LocalDate.parse(availableTime.getDate()), testUserInfo.getUuid());
            assertAll(
                    "Проверяем по дате " + availableTime.getDate(),
                    () -> assertThat(
                            "Количество прогулок на день совпадает с ожидаемым",
                            availableTime.getWalkCount(),
                            equalTo(
                                    dayLimitFunctions
                                            .getById(
                                                    new DayLimitPK().setDate(LocalDate.parse(availableTime.getDate()))
                                                            .setPersonId(testUserInfo.getUuid()))
                                            .getWalkCount())),
                    () -> assertThat(
                            "Данные выставленных временных промежутков соответствуют ожидаемым",
                            toAvailableTime(availableTime, testUserInfo.getUuid()),
                            equalTo(timeFrames)));
        });
    }

    private List<AvailableTime> toAvailableTime(UserSchedule availableTime, UUID personId) {
        var userTimeFrames = availableTime.getTimeFrames();
        var resp = new ArrayList<AvailableTime>();
        userTimeFrames.forEach(
                timeFrame -> resp.add(
                        new AvailableTime().setDate(LocalDate.parse(availableTime.getDate()))
                                .setId(timeFrame.getAvailableTimeId())
                                .setTimeFrom(OffsetTime.parse(timeFrame.getTimeFrom()))
                                .setTimeUntil(OffsetTime.parse(timeFrame.getTimeUntil()))
                                .setPersonId(personId)
                                .setTimezone(availableTime.getTimezone())
                                .setCity(timeFrame.getLocation().getCity())
                                .setBuilding(timeFrame.getLocation().getBuilding())
                                .setStreet(timeFrame.getLocation().getStreet())
                                .setClubsInFilter(timeFrame.getAvailableTimeClubsInFilter())
                                .setSearchAreaMeters(5000)
                                .setLocation(
                                        "POINT (%s %s)".formatted(
                                                timeFrame.getLocation().getLongitude(),
                                                timeFrame.getLocation().getLatitude()))));

        return resp;
    }
}
