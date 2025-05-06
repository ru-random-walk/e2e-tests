package random_walk.matcher.internal_controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.matcher.service.AppointmentMatcherApi;
import random_walk.automation.api.matcher.service.InternalMatcherApi;
import random_walk.automation.database.chat.functions.ChatMembersFunctions;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.automation.service.ChatService;
import random_walk.matcher.MatcherTest;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@Disabled("Жду ответа от Макса, возможно, доработки на его стороне")
class CreateChatAfterAppointmentRequestTest extends MatcherTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private InternalMatcherApi internalMatcherApi;

    @Autowired
    private ChatMembersFunctions chatMembersFunctions;

    @Autowired
    private AppointmentMatcherApi appointmentMatcherApi;

    private UUID appointmentId;

    @Test
    @DisplayName("Проверка создания чата после назначения встречи")
    void checkCreatingChatAfterAppointmentRequest() {
        var autotestUserInfo = userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER);
        var testUserInfo = userConfigService.getUserByRole(UserRoleEnum.TEST_USER);

        try {
            chatService.deleteChatBetweenUsers(autotestUserInfo.getUuid(), testUserInfo.getUuid());
        } catch (Exception ignored) {
        }

        var startsAt = OffsetDateTime.now().plusHours(1);
        var appointmentRequest = internalMatcherApi
                .getAppointmentRequest(autotestUserInfo.getUuid(), testUserInfo.getUuid(), startsAt, LONGITUDE, LATITUDE);

        appointmentId = appointmentRequest.getId();

        appointmentMatcherApi.approveAppointment(appointmentRequest.getId(), testTokenConfig.getToken());

        assertThat(
                "Проверяем, что чат был создан",
                chatMembersFunctions.getUsersChat(autotestUserInfo.getUuid(), testUserInfo.getUuid()),
                notNullValue());
    }

    @AfterEach
    public void deleteAppointment() {
        matcherService.deleteAppointmentRequest(appointmentId);
    }
}
