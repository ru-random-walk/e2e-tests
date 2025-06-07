package random_walk.matcher.available_time_controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.matcher.service.PersonMatcherApi;
import random_walk.automation.database.matcher.functions.AvailableTimeFunctions;
import random_walk.automation.domain.enums.ClubRole;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.matcher.MatcherTest;
import ru.testit.annotations.DisplayName;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

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

    private UUID testUserId;

    private UUID clubId;

    @Test
    @DisplayName("Проверка удаления свободного времени из базы данных")
    void checkDeleteAvailableTimeFromDatabase() {
        givenStep();

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

        thenStep();

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
        givenNonExistStep();
        var availableTimeId = UUID.randomUUID();

        var notFoundError = toDefaultErrorResponse(() -> availableTimeMatcherApi.deleteAvailableTime(availableTimeId));

        var errorMessage = "Available time with id %s is not found".formatted(availableTimeId);
        var errorCode = 404;

        checkError(notFoundError, errorCode, errorMessage);
    }

    @Step
    @Title("GIVEN: Получена информация о тестовом пользователе и группе, в которой он состоит")
    public void givenStep() {
        testUserId = userConfigService.getUserByRole(UserRoleEnum.TEST_USER).getUuid();
        clubId = clubConfigService.getClubByRole(ClubRole.DEFAULT_CLUB).getId();
    }

    @Step
    @Title("THEN: Свободное время пользователя успешно удалено")
    public void thenStep() {
    }

    @Step
    @Title("GIVEN: Получен id несуществующего свободного времени")
    public void givenNonExistStep() {
    }
}
