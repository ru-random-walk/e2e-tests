package random_walk.matcher.appointment_controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.matcher.service.AppointmentMatcherApi;
import random_walk.automation.api.matcher.service.InternalMatcherApi;
import random_walk.automation.api.matcher.service.PersonMatcherApi;
import random_walk.automation.domain.User;
import random_walk.matcher.MatcherTest;
import ru.testit.annotations.Description;
import ru.testit.annotations.DisplayName;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static random_walk.asserts.ErrorAsserts.checkError;
import static random_walk.automation.domain.enums.UserRoleEnum.AUTOTEST_USER;
import static random_walk.automation.domain.enums.UserRoleEnum.TEST_USER;
import static random_walk.automation.util.ExceptionUtils.toDefaultErrorResponse;

class ApproveAppointmentTest extends MatcherTest {

    @Autowired
    private InternalMatcherApi internalMatcherApi;

    @Autowired
    private AppointmentMatcherApi appointmentMatcherApi;

    @Autowired
    private PersonMatcherApi personMatcherApi;

    private UUID appointmentId;

    private User autotestUserInfo;

    private User testUserInfo;

    @Test
    @DisplayName("Подтверждение встречи пользователем, предложившим ее")
    void checkApproveAppointmentByRequester() {
        givenStep();

        var startsAt = OffsetDateTime.now().plusHours(1);
        var appointmentRequest = internalMatcherApi
                .getAppointmentRequest(autotestUserInfo.getUuid(), testUserInfo.getUuid(), startsAt, LONGITUDE, LATITUDE);

        appointmentId = appointmentRequest.getId();

        var accessError = toDefaultErrorResponse(
                () -> appointmentMatcherApi.approveAppointment(appointmentRequest.getId(), testTokenConfig.getAutotestToken()));

        var errorCode = 403;
        var errorMessage = "You cannot approve this appointment";

        checkError(accessError, errorCode, errorMessage);
    }

    @Test
    @DisplayName("Повторное подтверждение встречи")
    void checkApproveAlreadyApprovedAppointment() {
        givenStep();

        var startsAt = OffsetDateTime.now().plusHours(1);
        var appointmentRequest = internalMatcherApi
                .getAppointmentRequest(autotestUserInfo.getUuid(), testUserInfo.getUuid(), startsAt, LONGITUDE, LATITUDE);

        appointmentId = appointmentRequest.getId();

        appointmentMatcherApi.approveAppointment(appointmentRequest.getId(), testTokenConfig.getToken());

        var alreadyApprovedAppointmentError = toDefaultErrorResponse(
                () -> appointmentMatcherApi.approveAppointment(appointmentRequest.getId(), testTokenConfig.getToken()));

        var errorCode = 400;
        var errorMessage = "Appointment is not requested";

        checkError(alreadyApprovedAppointmentError, errorCode, errorMessage);
    }

    @Test
    @DisplayName("Подтверждение отмененной встречи")
    void checkApproveCancelledAppointment() {
        givenStep();

        var startsAt = OffsetDateTime.now().plusHours(1);
        var appointmentRequest = internalMatcherApi
                .getAppointmentRequest(autotestUserInfo.getUuid(), testUserInfo.getUuid(), startsAt, LONGITUDE, LATITUDE);

        appointmentId = appointmentRequest.getId();

        appointmentMatcherApi.rejectAppointment(appointmentRequest.getId(), testTokenConfig.getToken());

        var approveCancelledAppointmentError = toDefaultErrorResponse(
                () -> appointmentMatcherApi.approveAppointment(appointmentRequest.getId(), testTokenConfig.getToken()));

        var errorCode = 400;
        var errorMessage = "Appointment is not requested";

        checkError(approveCancelledAppointmentError, errorCode, errorMessage);
    }

    @Test
    @DisplayName("Проверка расписания пользователей после назначенной встречи")
    void checkScheduleAfterAppointmentRequest() {
        givenStep();

        var offsetDateTime = OffsetDateTime.now();
        var startsAt = OffsetDateTime
                .of(
                        offsetDateTime.getYear(),
                        offsetDateTime.getMonthValue(),
                        offsetDateTime.getDayOfMonth(),
                        22,
                        30,
                        0,
                        offsetDateTime.getNano(),
                        offsetDateTime.getOffset())
                .withOffsetSameInstant(ZoneOffset.UTC);
        var appointmentRequest = internalMatcherApi
                .getAppointmentRequest(autotestUserInfo.getUuid(), testUserInfo.getUuid(), startsAt, LONGITUDE, LATITUDE);

        appointmentId = appointmentRequest.getId();

        appointmentMatcherApi.approveAppointment(appointmentRequest.getId(), testTokenConfig.getToken());

        var testSchedule = Arrays.stream(personMatcherApi.getInfoAboutSchedule(testTokenConfig.getToken()))
                .filter(r -> r.getDate().equals(LocalDate.now().toString()))
                .findFirst()
                .orElse(null)
                .getTimeFrames();

        thenStep();

        assertAll(
                () -> assertThat(
                        "После назначения встречи available_time разделилось, второй диапазон не создан",
                        testSchedule.stream().filter(r -> r.getTimeUntil().equals("22:30+03:00")).findFirst().get(),
                        notNullValue()),

                () -> assertThat(
                        "Второй диапазон не был создан из-за промежутка, меньшего, чем 1 час",
                        testSchedule.stream().filter(r -> r.getTimeFrom().equals("22:30+03:00")).toList().size(),
                        equalTo(0)));
    }

    @Step
    @Title("GIVEN: Получена информация о тестовых пользователях")
    @Description("Пользователи - 58e953ef-0153-4918-9a26-17bcb2213c12 и 490689d5-4e63-4724-8ab5-4fb32750b263")
    public void givenStep() {
        testUserInfo = userConfigService.getUserByRole(TEST_USER);
        autotestUserInfo = userConfigService.getUserByRole(AUTOTEST_USER);
    }

    @Step
    @Title("THEN: Проверяем расписание пользователей после назначенной встречи")
    public void thenStep() {
    }

    @AfterEach
    @Step
    @Title("Удаление запроса на встречу, созданного в тесте")
    void deleteAppointment() {
        if (appointmentId != null) {
            matcherService.deleteAppointmentRequest(appointmentId);
            appointmentId = null;
        }
    }
}
