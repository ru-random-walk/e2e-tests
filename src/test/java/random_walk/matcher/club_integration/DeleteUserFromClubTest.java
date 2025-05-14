package random_walk.matcher.club_integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.club.services.MemberControllerApi;
import random_walk.automation.database.matcher.functions.PersonClubFunctions;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.matcher.MatcherTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DeleteUserFromClubTest extends MatcherTest {

    @Autowired
    private PersonClubFunctions personClubFunctions;

    @Autowired
    private MemberControllerApi memberControllerApi;

    @BeforeAll
    void addMemberInClub() {
        var userInfo = userConfigService.getUserByRole(UserRoleEnum.PERSONAL_ACCOUNT);

        var clubId = personClubFunctions.getUserClubs(userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER).getUuid())
                .get(0);

        memberControllerApi.addMemberInClub(clubId, userInfo.getUuid(), testTokenConfig.getAutotestToken());
    }

    @Test
    @DisplayName("Проверка удаления клуба, из которого выходит пользователь")
    void checkDeleteClubInMatcher() {
        var userInfo = userConfigService.getUserByRole(UserRoleEnum.PERSONAL_ACCOUNT);

        var clubId = personClubFunctions.getUserClubs(userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER).getUuid())
                .get(0);

        memberControllerApi.removeMemberFromClub(clubId, userInfo.getUuid(), testTokenConfig.getAutotestToken());

        assertThat(
                "Клуб %s не входит в список клубов пользователя".formatted(clubId),
                personClubFunctions.getUserClubs(userInfo.getUuid()).contains(clubId),
                is(false));
    }
}
