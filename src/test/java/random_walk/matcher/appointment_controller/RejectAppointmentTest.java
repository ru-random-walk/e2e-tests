package random_walk.matcher.appointment_controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.matcher.service.AppointmentMatcherApi;
import random_walk.automation.api.matcher.service.InternalMatcherApi;
import random_walk.automation.domain.User;
import random_walk.matcher.MatcherTest;
import ru.testit.annotations.Description;
import ru.testit.annotations.DisplayName;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.time.OffsetDateTime;
import java.util.UUID;

import static random_walk.asserts.ErrorAsserts.checkError;
import static random_walk.automation.domain.enums.UserRoleEnum.AUTOTEST_USER;
import static random_walk.automation.domain.enums.UserRoleEnum.TEST_USER;
import static random_walk.automation.util.ExceptionUtils.toDefaultErrorResponse;

class RejectAppointmentTest extends MatcherTest {

    @Autowired
    private InternalMatcherApi internalMatcherApi;

    @Autowired
    private AppointmentMatcherApi appointmentMatcherApi;

    private UUID appointmentId;

    private User testUserInfo;

    private User autotestUserInfo;

    @Test
    @DisplayName("Отмена встречи пользователем, назначившим ее")
    void checkRejectAppointmentByRequester() {
        givenStep();

        var startsAt = OffsetDateTime.now().plusHours(1);
        var appointmentRequest = internalMatcherApi
                .getAppointmentRequest(autotestUserInfo.getUuid(), testUserInfo.getUuid(), startsAt, LONGITUDE, LATITUDE);

        appointmentId = appointmentRequest.getId();

        var accessError = toDefaultErrorResponse(
                () -> appointmentMatcherApi.rejectAppointment(appointmentRequest.getId(), testTokenConfig.getAutotestToken()));

        var errorCode = 403;
        var errorMessage = "You cannot approve this appointment";

        checkError(accessError, errorCode, errorMessage);
    }

    @Test
    @DisplayName("Отмена уже отмененной встречи")
    void checkRejectAlreadyRejectedAppointment() {
        givenStep();

        var startsAt = OffsetDateTime.now().plusHours(1);
        var appointmentRequest = internalMatcherApi
                .getAppointmentRequest(autotestUserInfo.getUuid(), testUserInfo.getUuid(), startsAt, LONGITUDE, LATITUDE);

        appointmentId = appointmentRequest.getId();

        appointmentMatcherApi.rejectAppointment(appointmentRequest.getId(), testTokenConfig.getToken());

        var rejectAlreadyRejectedAppointmentError = toDefaultErrorResponse(
                () -> appointmentMatcherApi.rejectAppointment(appointmentRequest.getId(), testTokenConfig.getToken()));

        var errorCode = 400;
        var errorMessage = "Appointment is not requested";

        checkError(rejectAlreadyRejectedAppointmentError, errorCode, errorMessage);
    }

    @Test
    @DisplayName("Отмена принятой встречи")
    void checkRejectApprovedAppointment() {
        givenStep();

        var startsAt = OffsetDateTime.now().plusHours(1);
        var appointmentRequest = internalMatcherApi
                .getAppointmentRequest(autotestUserInfo.getUuid(), testUserInfo.getUuid(), startsAt, LONGITUDE, LATITUDE);

        appointmentId = appointmentRequest.getId();

        appointmentMatcherApi.approveAppointment(appointmentRequest.getId(), testTokenConfig.getToken());

        var rejectApprovedAppointmentError = toDefaultErrorResponse(
                () -> appointmentMatcherApi.rejectAppointment(appointmentRequest.getId(), testTokenConfig.getToken()));

        var errorCode = 400;
        var errorMessage = "Appointment is not requested";

        checkError(rejectApprovedAppointmentError, errorCode, errorMessage);
    }

    @Step
    @Title("GIVEN: Получена информация о тестовых пользователях")
    @Description("Пользователи - 58e953ef-0153-4918-9a26-17bcb2213c12 и 490689d5-4e63-4724-8ab5-4fb32750b263")
    public void givenStep() {
        testUserInfo = userConfigService.getUserByRole(TEST_USER);
        autotestUserInfo = userConfigService.getUserByRole(AUTOTEST_USER);
    }

    @AfterEach
    @Step
    @Title("Удаление созданного запроса на прогулку")
    void deleteAppointment() {
        if (appointmentId != null) {
            matcherService.deleteAppointmentRequest(appointmentId);
            appointmentId = null;
        }
    }
}
