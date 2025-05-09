package random_walk.matcher.available_time_controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.matcher.service.AvailableTimeMatcherApi;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.automation.service.ChatService;
import random_walk.matcher.MatcherTest;

import java.time.LocalDate;
import java.time.OffsetTime;

public class AddAvailableTimeTest extends MatcherTest {

    @Autowired
    private AvailableTimeMatcherApi availableTimeMatcherApi;

    @Autowired
    private ChatService chatService;

    @BeforeAll
    void deleteUsersChat() {
        try {
            chatService.deleteChatBetweenUsers(
                    userConfigService.getUserByRole(UserRoleEnum.TEST_USER).getUuid(),
                    userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER).getUuid());
        } catch (Exception ignored) {
        }
    }

    @Test
    @DisplayName("Добавление свободного времени для пользователя")
    void addAvailableTimeForUser() {

        var autotestUserInfo = userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER);
        var offsetTime = OffsetTime.now();
        var clubId = personClubFunctions.getUserClubs(autotestUserInfo.getUuid()).get(0);
        availableTimeMatcherApi.addAvailableTime(
                testTokenConfig.getAutotestToken(),
                clubId,
                offsetTime,
                OffsetTime.of(23, 59, 0, offsetTime.getNano(), offsetTime.getOffset()),
                LocalDate.now(),
                LATITUDE,
                LONGITUDE);

    }
}
