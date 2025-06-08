package random_walk.matcher.appointment_controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.matcher.service.AppointmentMatcherApi;
import random_walk.automation.api.matcher.service.InternalMatcherApi;
import random_walk.automation.database.matcher.functions.AppointmentDetailsFunctions;
import random_walk.automation.database.matcher.functions.AppointmentFunctions;
import random_walk.automation.domain.User;
import random_walk.automation.util.PointConverterUtils;
import random_walk.matcher.MatcherTest;
import ru.random_walk.swagger.matcher_service.model.AppointmentDetailsDto;
import ru.testit.annotations.Description;
import ru.testit.annotations.DisplayName;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static random_walk.asserts.ErrorAsserts.checkError;
import static random_walk.automation.domain.enums.UserRoleEnum.AUTOTEST_USER;
import static random_walk.automation.domain.enums.UserRoleEnum.TEST_USER;
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

    private User testUserInfo;

    private User autotestUserInfo;

    @Test
    @DisplayName("Получение информации о назначенной встрече")
    void getInfoAboutAppointment() {
        givenStep();

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

        thenStep();

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

    @Step
    @Title("GIVEN: Получена информация о тестовых пользователях")
    @Description("Пользователи - 58e953ef-0153-4918-9a26-17bcb2213c12 и 490689d5-4e63-4724-8ab5-4fb32750b263")
    public void givenStep() {
        testUserInfo = userConfigService.getUserByRole(TEST_USER);
        autotestUserInfo = userConfigService.getUserByRole(AUTOTEST_USER);
    }

    @Step
    @Title("THEN: Информация о встрече успешно получена")
    public void thenStep() {
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
