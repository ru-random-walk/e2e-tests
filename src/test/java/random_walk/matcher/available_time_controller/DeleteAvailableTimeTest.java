package random_walk.matcher.available_time_controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.matcher.service.PersonMatcherApi;
import random_walk.automation.database.matcher.functions.AvailableTimeFunctions;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.matcher.MatcherTest;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static random_walk.asserts.ErrorAsserts.checkError;
import static random_walk.automation.util.ExceptionUtils.toDefaultErrorResponse;

public class DeleteAvailableTimeTest extends MatcherTest {

    @Autowired
    private AvailableTimeFunctions availableTimeFunctions;

    @Autowired
    private PersonMatcherApi personMatcherApi;

    @Test
    @DisplayName("Проверка удаления свободного времени из базы данных")
    void checkDeleteAvailableTimeFromDatabase() {
        var testUserId = userConfigService.getUserByRole(UserRoleEnum.TEST_USER).getUuid();

        var clubId = personClubFunctions.getUserClubs(testUserId).get(0);

        var offsetTime = OffsetTime.now();
        var date = LocalDate.now().plusDays(2);

        availableTimeMatcherApi.addAvailableTime(
                testTokenConfig.getToken(),
                clubId,
                offsetTime,
                OffsetTime.of(23, 59, 0, offsetTime.getNano(), offsetTime.getOffset()),
                date,
                LATITUDE,
                LONGITUDE);

        var availableTimeId = availableTimeFunctions.getUserAvailableTimeByDateAndPersonId(date, testUserId).get(0).getId();

        availableTimeMatcherApi.deleteAvailableTime(availableTimeId);

        var testUserSchedule = personMatcherApi.getInfoAboutSchedule(testTokenConfig.getToken());

        var testUserScheduleOnCurrentDate = Arrays.stream(testUserSchedule)
                .filter(r -> r.getDate().equals(date.toString()))
                .findFirst();
        var isAvailableTimeIdInSchedule = testUserScheduleOnCurrentDate
                .map(
                        userSchedule -> userSchedule.getTimeFrames()
                                .stream()
                                .filter(r -> r.getAvailableTimeId().equals(availableTimeId))
                                .toList()
                                .isEmpty())
                .orElse(false);

        assertAll(
                "Проверяем корректность удаления свободного времени",
                () -> assertThat(
                        "Время для прогулки было удалено из базы данных",
                        availableTimeFunctions.getById(availableTimeId),
                        nullValue()),
                () -> assertThat(
                        "Удаленный слот не отображается в расписании свободного времени",
                        isAvailableTimeIdInSchedule,
                        equalTo(false)));
    }

    @Test
    @DisplayName("Удаление несуществующего свободного времени")
    void checkDeleteNonExistingAvailableTime() {
        var availableTimeId = UUID.randomUUID();

        var notFoundError = toDefaultErrorResponse(() -> availableTimeMatcherApi.deleteAvailableTime(availableTimeId));

        var errorMessage = "Available time with id %s is not found".formatted(availableTimeId);
        var errorCode = 404;

        checkError(notFoundError, errorCode, errorMessage);
    }
}
