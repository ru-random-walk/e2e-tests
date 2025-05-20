package random_walk.matcher.appointment_controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.matcher.service.AppointmentMatcherApi;
import random_walk.automation.api.matcher.service.InternalMatcherApi;
import random_walk.automation.database.matcher.functions.AppointmentDetailsFunctions;
import random_walk.automation.database.matcher.functions.AppointmentFunctions;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.automation.util.PointConverterUtils;
import random_walk.matcher.MatcherTest;
import ru.random_walk.swagger.matcher_service.model.AppointmentDetailsDto;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static random_walk.asserts.ErrorAsserts.checkError;
import static random_walk.automation.util.ExceptionUtils.toDefaultErrorResponse;

class GetAppointmentDetailsTest extends MatcherTest {

    private UUID appointmentId;

    @Autowired
    private InternalMatcherApi internalMatcherApi;

    @Autowired
    private AppointmentMatcherApi appointmentMatcherApi;

    @Autowired
    private AppointmentDetailsFunctions appointmentDetailsFunctions;

    @Autowired
    private AppointmentFunctions appointmentFunctions;

    @Test
    @DisplayName("Получение информации о назначенной встрече")
    void getInfoAboutAppointment() {
        var autotestUserInfo = userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER);
        var testUserInfo = userConfigService.getUserByRole(UserRoleEnum.TEST_USER);

        var startsAt = OffsetDateTime.now().plusHours(1);
        var appointmentRequest = internalMatcherApi
                .getAppointmentRequest(autotestUserInfo.getUuid(), testUserInfo.getUuid(), startsAt, LONGITUDE, LATITUDE);

        appointmentId = appointmentRequest.getId();

        var appointmentDetails = appointmentMatcherApi.getInfoByAppointment(appointmentId, testTokenConfig.getToken());

        var databaseAppointment = appointmentDetailsFunctions.getById(appointmentId);

        var appointmentParticipants = appointmentFunctions.getAppointmentParticipants(appointmentId);

        var coordinates = PointConverterUtils.getPointCoordinatesFromString(databaseAppointment.getApproximateLocation());

        var expectedResponse = new AppointmentDetailsDto().id(appointmentId)
                .endedAt(getUtcTimeZone(databaseAppointment.getEndedAt()))
                .participants(appointmentParticipants)
                .status(databaseAppointment.getStatus())
                .startsAt(getUtcTimeZone(databaseAppointment.getStartsAt()))
                .updatedAt(getUtcTimeZone(databaseAppointment.getUpdatedAt()))
                .longitude(coordinates.get(0))
                .latitude(coordinates.get(1));

        assertThat("Параметры возвращенной встречи соответствуют ожидаемым", appointmentDetails, equalTo(expectedResponse));
    }

    @Test
    @DisplayName("Получение несуществующей встречи")
    void getNonExistingAppointment() {
        var appointmentDetails = toDefaultErrorResponse(
                () -> appointmentMatcherApi.getInfoByAppointment(UUID.randomUUID(), testTokenConfig.getToken()));

        var errorCode = 403;
        var errorMessage = "Access is forbidden";

        checkError(appointmentDetails, errorCode, errorMessage);
    }

    private OffsetDateTime getUtcTimeZone(OffsetDateTime time) {
        if (time == null)
            return null;
        else
            return time.withOffsetSameInstant(ZoneOffset.UTC);
    }

    @AfterEach
    void deleteAppointment() {
        if (appointmentId != null) {
            matcherService.deleteAppointmentRequest(appointmentId);
            appointmentId = null;
        }
    }
}
