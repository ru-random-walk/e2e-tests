package random_walk.matcher.appointment_controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.matcher.service.AppointmentMatcherApi;
import random_walk.automation.api.matcher.service.InternalMatcherApi;
import random_walk.automation.database.matcher.functions.AppointmentDetailsFunctions;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.matcher.MatcherTest;
import ru.random_walk.swagger.matcher_service.model.AppointmentDetailsDto;

import java.time.OffsetDateTime;
import java.util.UUID;

import static java.lang.Thread.sleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ChangeAppointmentStatusTest extends MatcherTest {

    private UUID appointmentId;

    @Autowired
    private InternalMatcherApi internalMatcherApi;

    @Autowired
    private AppointmentMatcherApi appointmentMatcherApi;

    @Autowired
    private AppointmentDetailsFunctions appointmentDetailsFunctions;

    @Test
    @DisplayName("Проверка смены статуса встречи после ее подтверждения")
    void checkChangeAppointmentStatusAfterAgreement() {
        var autotestUserInfo = userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER);
        var testUserInfo = userConfigService.getUserByRole(UserRoleEnum.TEST_USER);

        var startsAt = OffsetDateTime.now().plusHours(1);
        var appointmentRequest = internalMatcherApi
                .getAppointmentRequest(autotestUserInfo.getUuid(), testUserInfo.getUuid(), startsAt, LONGITUDE, LATITUDE);

        appointmentId = appointmentRequest.getId();

        appointmentMatcherApi.approveAppointment(appointmentId, testTokenConfig.getToken());

        assertThat(
                "Проверяем переход встречи в нужный статус",
                appointmentDetailsFunctions.getById(appointmentId).getStatus(),
                equalTo(AppointmentDetailsDto.StatusEnum.APPOINTED));
    }

    @Test
    @DisplayName("Проверка смены статуса встречи после ее отмены")
    void checkChangeAppointmentStatusAfterDisagreement() {
        var autotestUserInfo = userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER);
        var testUserInfo = userConfigService.getUserByRole(UserRoleEnum.TEST_USER);

        var startsAt = OffsetDateTime.now().plusHours(1);
        var appointmentRequest = internalMatcherApi
                .getAppointmentRequest(autotestUserInfo.getUuid(), testUserInfo.getUuid(), startsAt, LONGITUDE, LATITUDE);

        appointmentId = appointmentRequest.getId();

        appointmentMatcherApi.rejectAppointment(appointmentId, testTokenConfig.getToken());

        assertThat(
                "Проверяем переход встречи в нужный статус",
                appointmentDetailsFunctions.getById(appointmentId).getStatus(),
                equalTo(AppointmentDetailsDto.StatusEnum.CANCELED));
    }

    @Test
    @DisplayName("Проверка перехода встречи в нужный статус после ее начала")
    void checkChangeAppointmentStatusAfterBegin() throws InterruptedException {
        var autotestUserInfo = userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER);
        var testUserInfo = userConfigService.getUserByRole(UserRoleEnum.TEST_USER);

        var startsAt = OffsetDateTime.now().plusSeconds(7);
        var appointmentRequest = internalMatcherApi
                .getAppointmentRequest(autotestUserInfo.getUuid(), testUserInfo.getUuid(), startsAt, LONGITUDE, LATITUDE);

        appointmentId = appointmentRequest.getId();

        appointmentMatcherApi.approveAppointment(appointmentId, testTokenConfig.getToken());

        sleep(7000);

        assertThat(
                "Проверяем переход встречи в нужный статус",
                appointmentDetailsFunctions.getById(appointmentId).getStatus(),
                equalTo(AppointmentDetailsDto.StatusEnum.IN_PROGRESS));
    }

    @AfterEach
    void deleteAppointmentRequest() {
        if (appointmentId != null) {
            matcherService.deleteAppointmentRequest(appointmentId);
            appointmentId = null;
        }
    }
}
