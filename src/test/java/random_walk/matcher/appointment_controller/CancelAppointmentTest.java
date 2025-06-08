package random_walk.matcher.appointment_controller;

import org.awaitility.Awaitility;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.matcher.service.AppointmentMatcherApi;
import random_walk.automation.database.matcher.entities.prkeys.DayLimitPK;
import random_walk.automation.database.matcher.functions.AppointmentFunctions;
import random_walk.automation.database.matcher.functions.AvailableTimeFunctions;
import random_walk.automation.database.matcher.functions.DayLimitFunctions;
import random_walk.automation.domain.enums.ClubRole;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.matcher.MatcherTest;
import ru.testit.annotations.Description;
import ru.testit.annotations.DisplayName;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static random_walk.asserts.ErrorAsserts.checkError;
import static random_walk.automation.domain.enums.UserRoleEnum.*;
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

    private UUID testUserId;

    private UUID autotestUserId;

    @Step
    @Title("Обновляем лимиты количества встреч для тестовых пользователей")
    @BeforeAll
    public void changeUserDayLimits() {
        var autotestUserId = userConfigService.getUserByRole(UserRoleEnum.FIRST_TEST_USER).getUuid();
        var testUserId = userConfigService.getUserByRole(UserRoleEnum.THIRD_TEST_USER).getUuid();

        availableTimeFunctions.deleteUserAvailableTime(autotestUserId);
        dayLimitFunctions.setDayLimitByDateAndPersonId(new DayLimitPK().setDate(LocalDate.now()).setPersonId(testUserId));
        dayLimitFunctions.setDayLimitByDateAndPersonId(new DayLimitPK().setDate(LocalDate.now()).setPersonId(autotestUserId));
    }

    @Test
    @DisplayName("Отмена прогулки пользователем")
    void checkCancelAppointment() {
        givenStep();

        var offsetTime = OffsetTime.now();
        var clubId = clubConfigService.getClubByRole(ClubRole.DEFAULT_CLUB).getId();
        availableTimeMatcherApi.addAvailableTime(
                userConfigService.getUserByRole(THIRD_TEST_USER).getAccessToken(),
                clubId,
                OffsetTime.of(15, 0, 0, offsetTime.getNano(), offsetTime.getOffset()),
                OffsetTime.of(17, 0, 0, offsetTime.getNano(), offsetTime.getOffset()),
                LocalDate.now().plusDays(2),
                LATITUDE,
                LONGITUDE);

        Awaitility.await()
                .atMost(80, TimeUnit.SECONDS)
                .pollInterval(10, TimeUnit.SECONDS)
                .until(
                        () -> appointmentFunctions.getUsersAppointment(autotestUserId, testUserId).size(),
                        Matchers.not(equalTo(0)));

        var appointmentId = appointmentFunctions.getUsersAppointment(autotestUserId, testUserId).get(0);

        appointmentMatcherApi.cancelAppointment(appointmentId, userConfigService.getUserByRole(FIRST_TEST_USER).getAccessToken());

        var testDayLimit = dayLimitFunctions.getById(new DayLimitPK().setPersonId(testUserId).setDate(LocalDate.now()));
        var autotestDayLimit = dayLimitFunctions.getById(new DayLimitPK().setPersonId(autotestUserId).setDate(LocalDate.now()));

        thenStep();

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
        givenStep();

        var notFoundError = toDefaultErrorResponse(
                () -> appointmentMatcherApi.cancelAppointment(UUID.randomUUID(), testTokenConfig.getToken()));

        var errorCode = 403;
        var errorMessage = "Access is forbidden";

        checkError(notFoundError, errorCode, errorMessage);
    }

    @Step
    @Title("GIVEN: Получена информация о тестовых пользователях")
    @Description("Пользователи - 58e953ef-0153-4918-9a26-17bcb2213c12 и 490689d5-4e63-4724-8ab5-4fb32750b263")
    public void givenStep() {
        testUserId = userConfigService.getUserByRole(FIRST_TEST_USER).getUuid();
        autotestUserId = userConfigService.getUserByRole(THIRD_TEST_USER).getUuid();
    }

    @Step
    @Title("THEN: Прогулка успешно отменена пользователем")
    public void thenStep() {
    }

    @AfterEach
    @Step
    @Title("Удаляем свободное время для пользователя AUTOTEST_USER")
    public void deleteAvailableTime() {
        try {
            availableTimeFunctions.deleteUserAvailableTime(userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER).getUuid());
        } catch (Exception ignored) {
        }
    }
}
