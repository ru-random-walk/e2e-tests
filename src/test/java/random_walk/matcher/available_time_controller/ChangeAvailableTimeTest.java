package random_walk.matcher.available_time_controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.matcher.service.AvailableTimeMatcherApi;
import random_walk.automation.database.matcher.functions.AvailableTimeFunctions;
import random_walk.automation.domain.enums.ClubRole;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.automation.util.PointConverterUtils;
import random_walk.matcher.MatcherTest;

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

public class ChangeAvailableTimeTest extends MatcherTest {

    @Autowired
    private AvailableTimeMatcherApi availableTimeMatcherApi;

    @Autowired
    private AvailableTimeFunctions availableTimeFunctions;

    private UUID availableTimeId;

    @Test
    @DisplayName("Изменение существующего свободного времени")
    void changeAvailableTime() {
        var testUserId = userConfigService.getUserByRole(UserRoleEnum.TEST_USER).getUuid();

        var clubId = clubConfigService.getClubByRole(ClubRole.DEFAULT_CLUB).getId();

        var offsetTime = OffsetTime.now(ZoneId.of("Europe/Moscow"));
        var timeUntil = OffsetTime.of(23, 59, 0, offsetTime.getNano(), offsetTime.getOffset());
        var date = LocalDate.now().plusDays(5);

        availableTimeMatcherApi
                .addAvailableTime(testTokenConfig.getToken(), clubId, offsetTime, timeUntil, date, LATITUDE, LONGITUDE);

        availableTimeId = availableTimeFunctions.getUserAvailableTimeByDateAndPersonId(date, testUserId).get(0).getId();

        var newLatitude = 56.304118;
        var newLongitude = 43.987741;
        var newTimeFrom = offsetTime.minusMinutes(10);
        var newTimeUntil = timeUntil.minusMinutes(10);

        availableTimeMatcherApi.changeAvailableTime(
                testTokenConfig.getToken(),
                availableTimeId,
                clubId,
                newTimeFrom,
                newTimeUntil,
                date,
                newLatitude,
                newLongitude);

        var changedAvailableTime = availableTimeFunctions.getUserAvailableTimeByDateAndPersonId(date, testUserId).get(0);

        assertAll(
                "Проверяем поля свободного времени после изменения",
                () -> assertThat(changedAvailableTime.getTimeFrom(), equalTo(newTimeFrom.truncatedTo(ChronoUnit.SECONDS))),
                () -> assertThat(changedAvailableTime.getTimeUntil(), equalTo(newTimeUntil.truncatedTo(ChronoUnit.SECONDS))),
                () -> assertThat(changedAvailableTime.getDate(), equalTo(date)),
                () -> assertThat(changedAvailableTime.getClubsInFilter(), equalTo(List.of(clubId))),
                () -> assertThat(changedAvailableTime.getPersonId(), equalTo(testUserId)),
                () -> assertThat(changedAvailableTime.getCity(), equalTo("Нижний Новгород")),
                () -> assertThat(changedAvailableTime.getStreet(), equalTo("Б. Покровская")),
                () -> assertThat(changedAvailableTime.getBuilding(), equalTo("100/1")),
                () -> assertThat(
                        PointConverterUtils.getPointCoordinatesFromString(changedAvailableTime.getLocation()),
                        equalTo(List.of(newLongitude, newLatitude))));
    }

    @Test
    @DisplayName("Проверка попытки изменения даты свободного времени")
    void changeDateOfAvailableTime() {
        var testUserId = userConfigService.getUserByRole(UserRoleEnum.TEST_USER).getUuid();

        var clubId = clubConfigService.getClubByRole(ClubRole.DEFAULT_CLUB).getId();

        var offsetTime = OffsetTime.now(ZoneId.of("Europe/Moscow"));
        var timeUntil = OffsetTime.of(23, 59, 0, offsetTime.getNano(), offsetTime.getOffset());
        var date = LocalDate.now().plusDays(5);

        availableTimeMatcherApi
                .addAvailableTime(testTokenConfig.getToken(), clubId, offsetTime, timeUntil, date, LATITUDE, LONGITUDE);

        availableTimeId = availableTimeFunctions.getUserAvailableTimeByDateAndPersonId(date, testUserId).get(0).getId();

        var changeAvailableTimeDate = toDefaultErrorResponse(
                () -> availableTimeMatcherApi.changeAvailableTime(
                        testTokenConfig.getToken(),
                        availableTimeId,
                        clubId,
                        offsetTime,
                        timeUntil,
                        LocalDate.now().plusDays(6),
                        LATITUDE,
                        LONGITUDE));

        var errorCode = 400;
        var errorMessage = "Date cannot be changed";

        checkError(changeAvailableTimeDate, errorCode, errorMessage);
    }

    @Test
    @DisplayName("Изменение свободного времени на время с разными таймзонами")
    void changeAvailableTimeWithDifferentTimeZone() {
        var testUserId = userConfigService.getUserByRole(UserRoleEnum.TEST_USER).getUuid();

        var clubId = clubConfigService.getClubByRole(ClubRole.DEFAULT_CLUB).getId();

        var offsetTime = OffsetTime.now(ZoneId.of("Europe/Moscow"));
        var timeUntil = OffsetTime.of(23, 59, 0, offsetTime.getNano(), offsetTime.getOffset());
        var date = LocalDate.now().plusDays(5);

        availableTimeMatcherApi
                .addAvailableTime(testTokenConfig.getToken(), clubId, offsetTime, timeUntil, date, LATITUDE, LONGITUDE);

        availableTimeId = availableTimeFunctions.getUserAvailableTimeByDateAndPersonId(date, testUserId).get(0).getId();

        var newTimeFrom = OffsetTime.now(ZoneId.of("Europe/Moscow"));
        var newTimeUntil = OffsetTime
                .of(23, 59, 0, OffsetTime.now(ZoneOffset.UTC).getNano(), OffsetTime.now(ZoneOffset.UTC).getOffset());

        var changeAvailableTimeWithDifferentTimeZone = toDefaultErrorResponse(
                () -> availableTimeMatcherApi.changeAvailableTime(
                        testTokenConfig.getToken(),
                        availableTimeId,
                        clubId,
                        newTimeFrom,
                        newTimeUntil,
                        date,
                        LATITUDE,
                        LONGITUDE));

        var errorCode = 400;
        var errorMessage = "Time frame offset should be same for timeFrom and timeUntil";

        checkError(changeAvailableTimeWithDifferentTimeZone, errorCode, errorMessage);
    }

    @Test
    @DisplayName("Изменение времени с датой начала большей, чем дата окончания")
    void changeAvailableTimeWithTimeFromGreaterThanTimeUntil() {
        var testUserId = userConfigService.getUserByRole(UserRoleEnum.TEST_USER).getUuid();

        var clubId = clubConfigService.getClubByRole(ClubRole.DEFAULT_CLUB).getId();

        var offsetTime = OffsetTime.now(ZoneId.of("Europe/Moscow"));
        var timeUntil = OffsetTime.of(23, 59, 0, offsetTime.getNano(), offsetTime.getOffset());
        var date = LocalDate.now().plusDays(5);

        availableTimeMatcherApi
                .addAvailableTime(testTokenConfig.getToken(), clubId, offsetTime, timeUntil, date, LATITUDE, LONGITUDE);

        availableTimeId = availableTimeFunctions.getUserAvailableTimeByDateAndPersonId(date, testUserId).get(0).getId();

        var changeAvailableTimeWithTimeFromGreaterThanTimeUntil = toDefaultErrorResponse(
                () -> availableTimeMatcherApi
                        .addAvailableTime(testTokenConfig.getToken(), clubId, timeUntil, offsetTime, date, LATITUDE, LONGITUDE));

        var errorCode = 400;
        var errorMessage = "Impossible time frame [%s; %s]".formatted(timeUntil, offsetTime);

        checkError(changeAvailableTimeWithTimeFromGreaterThanTimeUntil, errorCode, errorMessage);
    }

    @AfterEach
    void deleteAvailableTime() {
        if (availableTimeId != null) {
            availableTimeFunctions.deleteById(availableTimeId);
            availableTimeId = null;
        }
    }
}
