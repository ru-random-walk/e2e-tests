package random_walk.matcher.appointment_controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.matcher.service.AppointmentMatcherApi;
import random_walk.automation.api.matcher.service.InternalMatcherApi;
import random_walk.automation.database.matcher.functions.AppointmentFunctions;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.matcher.MatcherTest;

import java.time.OffsetDateTime;
import java.util.UUID;

import static random_walk.asserts.ErrorAsserts.checkError;
import static random_walk.automation.util.ExceptionUtils.toDefaultErrorResponse;

class RejectAppointmentTest extends MatcherTest {

    @Autowired
    private InternalMatcherApi internalMatcherApi;

    @Autowired
    private AppointmentMatcherApi appointmentMatcherApi;

    @Autowired
    private AppointmentFunctions appointmentFunctions;

    private UUID appointmentId;

    @Test
    @DisplayName("Отмена встречи пользователем, назначившим ее")
    void checkRejectAppointmentByRequester() {
        var autotestUserInfo = userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER);
        var testUserInfo = userConfigService.getUserByRole(UserRoleEnum.TEST_USER);

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
        var autotestUserInfo = userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER);
        var testUserInfo = userConfigService.getUserByRole(UserRoleEnum.TEST_USER);

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
        var autotestUserInfo = userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER);
        var testUserInfo = userConfigService.getUserByRole(UserRoleEnum.TEST_USER);

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

    @AfterEach
    void deleteAppointment() {
        if (appointmentId != null) {
            appointmentFunctions.deleteByAppointmentId(appointmentId);
            appointmentId = null;
        }
    }
}
