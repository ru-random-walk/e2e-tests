package random_walk.matcher.club_integration;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.club.services.MemberControllerApi;
import random_walk.automation.database.matcher.functions.PersonClubFunctions;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.matcher.MatcherTest;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AddUserToClubTest extends MatcherTest {

    @Autowired
    private PersonClubFunctions personClubFunctions;

    @Autowired
    private MemberControllerApi memberControllerApi;

    @BeforeEach
    void removeMemberFromClub() {
        var userInfo = userConfigService.getUserByRole(UserRoleEnum.PERSONAL_ACCOUNT);

        var clubId = personClubFunctions.getUserClubs(userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER).getUuid())
                .get(0);

        try {
            memberControllerApi.removeMemberFromClub(clubId, userInfo.getUuid(), testTokenConfig.getAutotestToken());
        } catch (Exception ignored) {}
    }

    @Test
    @DisplayName("Проверка добавления клуба для пользователя после его вступления")
    void checkAddClubInMatcher() {
        var userInfo = userConfigService.getUserByRole(UserRoleEnum.PERSONAL_ACCOUNT);

        var clubId = personClubFunctions.getUserClubs(userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER).getUuid())
                .get(0);

        memberControllerApi.addMemberInClub(clubId, userInfo.getUuid(), testTokenConfig.getAutotestToken());

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .until(() -> personClubFunctions.getUserClubs(userInfo.getUuid()).contains(clubId), is(true));

        assertThat(
                "Клуб %s входит в список клубов пользователя".formatted(clubId),
                personClubFunctions.getUserClubs(userInfo.getUuid()).contains(clubId),
                is(true));
    }
}
