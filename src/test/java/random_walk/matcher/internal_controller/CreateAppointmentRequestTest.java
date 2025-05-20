package random_walk.matcher.internal_controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.matcher.service.AppointmentMatcherApi;
import random_walk.automation.api.matcher.service.InternalMatcherApi;
import random_walk.automation.api.matcher.service.PersonMatcherApi;
import random_walk.automation.database.matcher.functions.AppointmentDetailsFunctions;
import random_walk.automation.database.matcher.functions.AppointmentFunctions;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.matcher.MatcherTest;
import ru.random_walk.swagger.matcher_service.model.AppointmentDetailsDto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static random_walk.automation.util.ExceptionUtils.toDefaultErrorResponse;

class CreateAppointmentRequestTest extends MatcherTest {

    @Autowired
    private InternalMatcherApi internalMatcherApi;

    @Autowired
    private AppointmentDetailsFunctions appointmentDetailsFunctions;

    @Autowired
    private AppointmentFunctions appointmentFunctions;

    @Autowired
    private PersonMatcherApi personMatcherApi;

    @Autowired
    private AppointmentMatcherApi appointmentMatcherApi;

    private UUID appointmentId;

    @Test
    @DisplayName("Проверка создания запроса на встречу")
    void checkCorrectAppointmentRequest() {
        var autotestUserInfo = userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER);
        var testUserInfo = userConfigService.getUserByRole(UserRoleEnum.TEST_USER);

        var startsAt = OffsetDateTime.now().plusHours(1);
        var appointmentRequest = internalMatcherApi
                .getAppointmentRequest(autotestUserInfo.getUuid(), testUserInfo.getUuid(), startsAt, LONGITUDE, LATITUDE);

        appointmentId = appointmentRequest.getId();

        var requesterId = appointmentDetailsFunctions.getById(appointmentRequest.getId()).getRequesterId();

        var appointmentParticipants = appointmentFunctions.getAppointmentParticipants(appointmentId);

        var testAppointmentSchedule = personMatcherApi.getInfoAboutSchedule(testTokenConfig.getToken());
        var autotestAppointmentSchedule = personMatcherApi.getInfoAboutSchedule(testTokenConfig.getAutotestToken());
        assertAll(
                "Проверяем детали предложенной встречи",
                () -> assertThat(
                        "Время начала прогулки соответствует ожидаемому",
                        appointmentRequest.getStartsAt(),
                        equalTo(startsAt.withOffsetSameInstant(ZoneOffset.UTC))),
                () -> assertThat(
                        "Статус прогулки = ожидает ответа",
                        appointmentRequest.getStatus(),
                        equalTo(AppointmentDetailsDto.StatusEnum.REQUESTED)),
                () -> assertThat(
                        "Долгота места встречи соответствует ожидаемому",
                        appointmentRequest.getLongitude(),
                        equalTo(LONGITUDE)),
                () -> assertThat(
                        "Широта места встречи соответствует ожидаемому",
                        appointmentRequest.getLatitude(),
                        equalTo(LATITUDE)),
                () -> assertThat(
                        "Пользователь, отправивший запрос на встречу, соответствует ожидаемому",
                        requesterId,
                        equalTo(autotestUserInfo.getUuid())),
                () -> assertThat(
                        "Список участников в базе соответствует ожидаемому",
                        appointmentRequest.getParticipants(),
                        equalTo(appointmentParticipants)),
                () -> assertThat(
                        "У пользователя %s прогулка отображается в расписании".formatted(testUserInfo.getUuid()),
                        Arrays.stream(testAppointmentSchedule)
                                .filter(r -> r.getDate().equals(LocalDate.now().toString()))
                                .findFirst()
                                .get()
                                .getTimeFrames()
                                .stream()
                                .anyMatch(r -> Objects.equals(r.getAppointmentId(), appointmentId)),
                        is(true)),
                () -> assertThat(
                        "У пользователя %s прогулка отображаается в расписании".formatted(autotestUserInfo.getUuid()),
                        Arrays.stream(autotestAppointmentSchedule)
                                .filter(r -> r.getDate().equals(LocalDate.now().toString()))
                                .findFirst()
                                .get()
                                .getTimeFrames()
                                .stream()
                                .anyMatch(r -> Objects.equals(r.getAppointmentId(), appointmentId)),
                        is(true)));
    }

    @Test
    @DisplayName("Попытка запросить прогулку для прошедшего времени")
    void checkAppointmentRequestInPast() {
        var autotestUserInfo = userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER);
        var testUserInfo = userConfigService.getUserByRole(UserRoleEnum.TEST_USER);

        var startsAt = OffsetDateTime.now().minusDays(1);
        var appointmentRequest = toDefaultErrorResponse(
                () -> internalMatcherApi.getAppointmentRequest(
                        autotestUserInfo.getUuid(),
                        testUserInfo.getUuid(),
                        startsAt,
                        LONGITUDE,
                        LATITUDE));

        var errorMessage = "Start time cannot be in the past";

        assertThat("Сообщение об ошибке соответствует ожидаемому", appointmentRequest.getMessage(), equalTo(errorMessage));
    }

    @Test
    @DisplayName("Проверка отсутствия отмененной встречи в расписании")
    void checkCancelAppointmentInSchedule() {
        var autotestUserInfo = userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER);
        var testUserInfo = userConfigService.getUserByRole(UserRoleEnum.TEST_USER);

        var startsAt = OffsetDateTime.now().plusHours(1);
        var appointmentRequest = internalMatcherApi
                .getAppointmentRequest(autotestUserInfo.getUuid(), testUserInfo.getUuid(), startsAt, LONGITUDE, LATITUDE);

        appointmentId = appointmentRequest.getId();

        // встреча отклонена партнером
        appointmentMatcherApi.rejectAppointment(appointmentId, testTokenConfig.getToken());

        var appointmentDetails = appointmentDetailsFunctions.getById(appointmentId);
        assertAll(
                "Проверяем параметры встречи",
                () -> assertThat("Проставлено время окончания встречи", appointmentDetails.getEndedAt(), notNullValue()),
                () -> assertThat(
                        "Встреча переведена в статус отмененной",
                        appointmentDetails.getStatus(),
                        equalTo(AppointmentDetailsDto.StatusEnum.CANCELED)));

        var testUserSchedule = personMatcherApi.getInfoAboutSchedule(testTokenConfig.getToken());
        var autotestUserSchedule = personMatcherApi.getInfoAboutSchedule(testTokenConfig.getAutotestToken());

        assertAll(
                "Отмененная встреча не отображается в расписании пользователей",
                () -> assertThat(
                        "У пользователя %s прогулка не отображается в расписании".formatted(testUserInfo.getUuid()),
                        Arrays.stream(testUserSchedule)
                                .filter(r -> r.getDate().equals(LocalDate.now().toString()))
                                .findFirst()
                                .get()
                                .getTimeFrames()
                                .stream()
                                .anyMatch(r -> Objects.equals(r.getAppointmentId(), appointmentId)),
                        is(false)),
                () -> {
                    if (autotestUserSchedule.length != 0) {
                        assertThat(
                                "У пользователя %s прогулка не отображается в расписании".formatted(autotestUserInfo.getUuid()),
                                Arrays.stream(autotestUserSchedule)
                                        .filter(r -> r.getDate().equals(LocalDate.now().toString()))
                                        .findFirst()
                                        .get()
                                        .getTimeFrames()
                                        .stream()
                                        .anyMatch(r -> Objects.equals(r.getAppointmentId(), appointmentId)),
                                is(false));
                    } else {
                        assertThat(autotestUserSchedule.length, equalTo(0));
                    }
                });
    }

    @Test
    @DisplayName("Невозможно запросить прогулку, если на запрашиваемое время уже есть встреча")
    void checkAppointmentRequestByPlannedTimeFrame() {
        var autotestUserInfo = userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER);
        var testUserInfo = userConfigService.getUserByRole(UserRoleEnum.TEST_USER);

        var startsAt = OffsetDateTime.now().plusHours(1);
        var appointmentRequest = internalMatcherApi
                .getAppointmentRequest(autotestUserInfo.getUuid(), testUserInfo.getUuid(), startsAt, LONGITUDE, LATITUDE);

        appointmentId = appointmentRequest.getId();

        // встреча принята партнером
        appointmentMatcherApi.approveAppointment(appointmentId, testTokenConfig.getToken());

        var secondAppointmentRequest = toDefaultErrorResponse(
                () -> internalMatcherApi.getAppointmentRequest(
                        autotestUserInfo.getUuid(),
                        testUserInfo.getUuid(),
                        startsAt,
                        LONGITUDE,
                        LATITUDE));

        var errorMessage = "Requested time conflicts with existing appointment";

        assertThat("Сообщение об ошибке соответствует ожидаемому", secondAppointmentRequest.getMessage(), equalTo(errorMessage));
    }

    @Test
    @DisplayName("Проверка автоматической отмены запроса на прогулку при принятии другого запроса на это время")
    void checkCancelAppointmentRequestAfterApproveOtherRequestAtSameTimeFrame() {
        var autotestUserInfo = userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER);
        var testUserInfo = userConfigService.getUserByRole(UserRoleEnum.TEST_USER);

        var startsAt = OffsetDateTime.now().plusHours(1);
        var appointmentRequest = internalMatcherApi
                .getAppointmentRequest(autotestUserInfo.getUuid(), testUserInfo.getUuid(), startsAt, LONGITUDE, LATITUDE);

        appointmentId = appointmentRequest.getId();

        var secondAppointmentRequest = internalMatcherApi
                .getAppointmentRequest(autotestUserInfo.getUuid(), testUserInfo.getUuid(), startsAt, LONGITUDE, LATITUDE);

        appointmentMatcherApi.approveAppointment(secondAppointmentRequest.getId(), testTokenConfig.getToken());

        var firstAppointmentDatabase = appointmentDetailsFunctions.getById(appointmentId);

        assertAll(
                "Проверяем параметры встречи",
                () -> assertThat("Проставлено время окончания встречи", firstAppointmentDatabase.getEndedAt(), notNullValue()),
                () -> assertThat(
                        "Встреча переведена в статус отмененной",
                        firstAppointmentDatabase.getStatus(),
                        equalTo(AppointmentDetailsDto.StatusEnum.CANCELED)));

        matcherService.deleteAppointmentRequest(secondAppointmentRequest.getId());
    }

    @AfterEach
    void deleteAppointmentRequest() {
        if (appointmentId != null) {
            matcherService.deleteAppointmentRequest(appointmentId);
            appointmentId = null;
        }
    }
}
