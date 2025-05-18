package random_walk.matcher.club_integration;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.club.services.ClubControllerApi;
import random_walk.automation.api.club.services.MemberControllerApi;
import random_walk.automation.database.matcher.functions.PersonClubFunctions;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.matcher.MatcherTest;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AddUserToClubTest extends MatcherTest {

    @Autowired
    private PersonClubFunctions personClubFunctions;

    @Autowired
    private MemberControllerApi memberControllerApi;

    @Autowired
    private ClubControllerApi clubControllerApi;

    private UUID clubId;

    @BeforeEach
    void removeMemberFromClub() {
        clubId = UUID.fromString(clubControllerApi.createClub("1", "2", testTokenConfig.getToken()).getId());
    }

    @Test
    @DisplayName("Проверка добавления клуба для пользователя после его вступления")
    void checkAddClubInMatcher() {
        var userInfo = userConfigService.getUserByRole(UserRoleEnum.PERSONAL_ACCOUNT);

        memberControllerApi.addMemberInClub(clubId, userInfo.getUuid(), testTokenConfig.getToken());

        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .until(() -> personClubFunctions.getUserClubs(userInfo.getUuid()).contains(clubId), is(true));

        assertThat(
                "Клуб %s входит в список клубов пользователя".formatted(clubId),
                personClubFunctions.getUserClubs(userInfo.getUuid()).contains(clubId),
                is(true));
    }

    @AfterEach
    void deleteClub() {
        clubControllerApi.removeClub(clubId, testTokenConfig.getToken());
    }
}
