package random_walk.matcher.internal_controller;

import org.awaitility.Awaitility;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.database.chat.functions.ChatMembersFunctions;
import random_walk.automation.database.matcher.functions.AppointmentFunctions;
import random_walk.automation.database.matcher.functions.AvailableTimeFunctions;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.automation.service.ChatService;
import random_walk.matcher.MatcherTest;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Disabled("Алгоритм исправил, но перестала назначаться встреча, отпишу Никите")
class CreateChatAfterAppointmentRequestTest extends MatcherTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatMembersFunctions chatMembersFunctions;

    @Autowired
    private AvailableTimeFunctions availableTimeFunctions;

    @Autowired
    private AppointmentFunctions appointmentFunctions;

    @BeforeAll
    void deleteAvailableTimeAndUserAppointments() {
        var autotestUserId = userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER).getUuid();
        var testUserId = userConfigService.getUserByRole(UserRoleEnum.TEST_USER).getUuid();
        try {
            chatService.deleteChatBetweenUsers(autotestUserId, testUserId);
        } catch (Exception ignored) {
        }
        availableTimeFunctions.deleteUserAvailableTime(autotestUserId);
        var appointmentIds = appointmentFunctions.getUsersAppointment(autotestUserId, testUserId);
        if (!appointmentIds.isEmpty()) {
            appointmentIds.forEach(appointmentId -> matcherService.deleteAppointmentRequest(appointmentId));
        }

    }

    @Test
    @DisplayName("Проверка создания чата после назначения встречи")
    void checkCreatingChatAfterAppointmentRequest() {
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

        assertThat(
                "Проверяем, что чат был создан",
                chatMembersFunctions.getUsersChat(autotestUserId, testUserId),
                notNullValue());
    }

    @AfterEach
    public void deleteAppointment() {
        var testUserId = userConfigService.getUserByRole(UserRoleEnum.TEST_USER).getUuid();
        var autotestUserId = userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER).getUuid();
        availableTimeFunctions.deleteUserAvailableTime(autotestUserId);
        try {
            chatService.deleteChatBetweenUsers(testUserId, autotestUserId);
        } catch (Exception ignored) {
        }
        try {
            matcherService.deleteAppointmentRequest(testUserId, autotestUserId);
        } catch (Exception ignored) {
        }
    }
}
