package random_walk.matcher.available_time_controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.matcher.service.AvailableTimeMatcherApi;
import random_walk.automation.database.matcher.functions.AvailableTimeFunctions;
import random_walk.automation.domain.enums.ClubRole;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.automation.util.PointConverterUtils;
import random_walk.matcher.MatcherTest;
import ru.testit.annotations.DisplayName;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static random_walk.asserts.ErrorAsserts.checkError;
import static random_walk.automation.util.ExceptionUtils.toDefaultErrorResponse;

class AddAvailableTimeTest extends MatcherTest {

    @Autowired
    private AvailableTimeMatcherApi availableTimeMatcherApi;

    @Autowired
    private AvailableTimeFunctions availableTimeFunctions;

    private UUID availableTimeId;

    private UUID testUserId;

    private UUID clubId;

    @Test
    @DisplayName("Добавление свободного времени для пользователя")
    void addAvailableTimeForUser() {
        givenStep();

        var offsetTime = OffsetTime.now(ZoneId.of("Europe/Moscow"));
        var timeUntil = OffsetTime.of(23, 59, 0, offsetTime.getNano(), offsetTime.getOffset());
        var date = LocalDate.now().plusDays(3);

        availableTimeMatcherApi
                .addAvailableTime(testTokenConfig.getToken(), clubId, offsetTime, timeUntil, date, LATITUDE, LONGITUDE);

        var testUserAvailableTime = availableTimeFunctions.getUserAvailableTimeByDateAndPersonId(date, testUserId).get(0);

        availableTimeId = testUserAvailableTime.getId();

        assertAll(
                "Проверяем поля времени",
                () -> assertThat(testUserAvailableTime.getTimeFrom(), equalTo(offsetTime.truncatedTo(ChronoUnit.SECONDS))),
                () -> assertThat(testUserAvailableTime.getTimeUntil(), equalTo(timeUntil.truncatedTo(ChronoUnit.SECONDS))),
                () -> assertThat(testUserAvailableTime.getDate(), equalTo(date)),
                () -> assertThat(testUserAvailableTime.getClubsInFilter(), equalTo(List.of(clubId))),
                () -> assertThat(testUserAvailableTime.getPersonId(), equalTo(testUserId)),
                () -> assertThat(testUserAvailableTime.getCity(), equalTo("Нижний Новгород")),
                () -> assertThat(testUserAvailableTime.getStreet(), equalTo("Б. Покровская")),
                () -> assertThat(testUserAvailableTime.getBuilding(), equalTo("100/1")),
                () -> assertThat(
                        PointConverterUtils.getPointCoordinatesFromString(testUserAvailableTime.getLocation()),
                        equalTo(List.of(LONGITUDE, LATITUDE))));
    }

    @Test
    @DisplayName("Добавление пересекающегося свободного времени")
    void addIntersectAvailableTime() {
        givenStep();

        var offsetTime = OffsetTime.now();
        var timeUntil = OffsetTime.of(23, 59, 0, offsetTime.getNano(), offsetTime.getOffset());
        var date = LocalDate.now().plusDays(3);

        availableTimeMatcherApi
                .addAvailableTime(testTokenConfig.getToken(), clubId, offsetTime, timeUntil, date, LATITUDE, LONGITUDE);

        availableTimeId = availableTimeFunctions.getUserAvailableTimeByDateAndPersonId(date, testUserId).get(0).getId();
        var availableTimeDb = availableTimeFunctions.getById(availableTimeId);

        var intersectAvailableTime = toDefaultErrorResponse(
                () -> availableTimeMatcherApi
                        .addAvailableTime(testTokenConfig.getToken(), clubId, offsetTime, timeUntil, date, LATITUDE, LONGITUDE));

        var errorCode = 400;
        var errorMessage = "Time frame %s [%s; %s] conflicts with existing available time %s [%s; %s]"
                .formatted(date, offsetTime, timeUntil, date, availableTimeDb.getTimeFrom(), availableTimeDb.getTimeUntil());

        checkError(intersectAvailableTime, errorCode, errorMessage);
    }

    @Test
    @DisplayName("Добавление свободного времени с разными таймзонами начала и конца")
    void addAvailableTimeWithDifferentTimeZone() {
        givenStep();

        var offsetTime = OffsetTime.now(ZoneId.of("Europe/Moscow"));
        var timeUntil = OffsetTime
                .of(23, 59, 0, OffsetTime.now(ZoneOffset.UTC).getNano(), OffsetTime.now(ZoneOffset.UTC).getOffset());

        var date = LocalDate.now().plusDays(3);

        var availableTimeWithDifferentTimeZone = toDefaultErrorResponse(
                () -> availableTimeMatcherApi
                        .addAvailableTime(testTokenConfig.getToken(), clubId, offsetTime, timeUntil, date, LATITUDE, LONGITUDE));

        var errorCode = 400;
        var errorMessage = "Time frame offset should be same for timeFrom and timeUntil";

        checkError(availableTimeWithDifferentTimeZone, errorCode, errorMessage);
    }

    @Test
    @DisplayName("Добавление времени с датой начала большей, чем дата окончания")
    void addAvailableTimeWithTimeFromGreaterThanTimeUntil() {
        givenStep();

        var offsetTime = OffsetTime.now();
        var timeUntil = OffsetTime.of(23, 59, 0, offsetTime.getNano(), offsetTime.getOffset());
        var date = LocalDate.now().plusDays(3);

        var timeFromGreaterThanTimeUntil = toDefaultErrorResponse(
                () -> availableTimeMatcherApi
                        .addAvailableTime(testTokenConfig.getToken(), clubId, timeUntil, offsetTime, date, LATITUDE, LONGITUDE));

        var errorCode = 400;
        var errorMessage = "Impossible time frame [%s; %s]".formatted(timeUntil, offsetTime);

        checkError(timeFromGreaterThanTimeUntil, errorCode, errorMessage);
    }

    @Step
    @Title("GIVEN: Получена информация о тестовом пользователе и группе, в которой он состоит")
    public void givenStep() {
        testUserId = userConfigService.getUserByRole(UserRoleEnum.TEST_USER).getUuid();
        clubId = clubConfigService.getClubByRole(ClubRole.DEFAULT_CLUB).getId();
    }

    @AfterEach
    @Step
    @Title("Удаление свободного времени, добавленного в тестах")
    void deleteAvailableTime() {
        if (availableTimeId != null) {
            availableTimeFunctions.deleteById(availableTimeId);
            availableTimeId = null;
        }
    }
}
