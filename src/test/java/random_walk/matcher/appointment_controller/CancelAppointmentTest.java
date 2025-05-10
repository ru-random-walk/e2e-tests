package random_walk.matcher.appointment_controller;

import org.awaitility.Awaitility;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.matcher.service.AppointmentMatcherApi;
import random_walk.automation.database.matcher.entities.prkeys.DayLimitPK;
import random_walk.automation.database.matcher.functions.AppointmentFunctions;
import random_walk.automation.database.matcher.functions.AvailableTimeFunctions;
import random_walk.automation.database.matcher.functions.DayLimitFunctions;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.matcher.MatcherTest;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static random_walk.asserts.ErrorAsserts.checkError;
import static random_walk.automation.util.ExceptionUtils.toDefaultErrorResponse;

public class CancelAppointmentTest extends MatcherTest {

    @Autowired
    private AppointmentMatcherApi appointmentMatcherApi;

    @Autowired
    private DayLimitFunctions dayLimitFunctions;

    @Autowired
    private AppointmentFunctions appointmentFunctions;

    @Autowired
    private AvailableTimeFunctions availableTimeFunctions;

    @BeforeAll
    public void changeUserDayLimits() {
        var autotestUserId = userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER).getUuid();
        var testUserId = userConfigService.getUserByRole(UserRoleEnum.TEST_USER).getUuid();

        availableTimeFunctions.deleteUserAvailableTime(autotestUserId);
        dayLimitFunctions.setDayLimitByDateAndPersonId(new DayLimitPK().setDate(LocalDate.now()).setPersonId(testUserId));
        dayLimitFunctions.setDayLimitByDateAndPersonId(new DayLimitPK().setDate(LocalDate.now()).setPersonId(autotestUserId));
    }

    @Test
    @DisplayName("Отмена прогулки пользователем")
    void checkCancelAppointment() {
        var autotestUserId = userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER).getUuid();
        var testUserId = userConfigService.getUserByRole(UserRoleEnum.TEST_USER).getUuid();

        var offsetTime = OffsetTime.now();
        var clubId = personClubFunctions.getUserClubs(autotestUserId).get(0);
        availableTimeMatcherApi.addAvailableTime(
                testTokenConfig.getAutotestToken(),
                clubId,
                offsetTime,
                OffsetTime.of(23, 59, 0, offsetTime.getNano(), offsetTime.getOffset()),
                LocalDate.now(),
                LATITUDE,
                LONGITUDE);

        Awaitility.await()
                .atMost(80, TimeUnit.SECONDS)
                .pollInterval(10, TimeUnit.SECONDS)
                .until(
                        () -> appointmentFunctions.getUsersAppointment(autotestUserId, testUserId).size(),
                        Matchers.not(equalTo(0)));

        var appointmentId = appointmentFunctions.getUsersAppointment(autotestUserId, testUserId).get(0);

        appointmentMatcherApi.cancelAppointment(appointmentId, testTokenConfig.getToken());

        var testDayLimit = dayLimitFunctions.getById(new DayLimitPK().setPersonId(testUserId).setDate(LocalDate.now()));
        var autotestDayLimit = dayLimitFunctions.getById(new DayLimitPK().setPersonId(autotestUserId).setDate(LocalDate.now()));

        assertAll(
                "Проверяем выставленные параметры после отмены встречи",
                () -> assertThat(
                        "Встреча удалена",
                        appointmentFunctions.getUsersAppointment(testUserId, autotestUserId).size(),
                        equalTo(0)),
                () -> assertThat(
                        "У пользователя, отменившего встречу, не восстановлена возможность найти прогулку",
                        testDayLimit.getWalkCount(),
                        equalTo(0)),
                () -> assertThat(
                        "У пользователя, для которого встречу отменили, попытка восстановлена",
                        autotestDayLimit.getWalkCount(),
                        equalTo(1)));
    }

    @Test
    @DisplayName("Отмена несуществующей встречи")
    void checkCancelNonExistingAppointment() {
        var notFoundError = toDefaultErrorResponse(
                () -> appointmentMatcherApi.cancelAppointment(UUID.randomUUID(), testTokenConfig.getToken()));

        var errorCode = 403;
        var errorMessage = "Access is forbidden";

        checkError(notFoundError, errorCode, errorMessage);
    }
}
