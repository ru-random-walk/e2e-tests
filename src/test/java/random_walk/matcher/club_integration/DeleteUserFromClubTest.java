package random_walk.matcher.club_integration;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.club.services.ClubControllerApi;
import random_walk.automation.api.club.services.MemberControllerApi;
import random_walk.automation.database.matcher.functions.PersonClubFunctions;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.matcher.MatcherTest;
import ru.testit.annotations.DisplayName;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DeleteUserFromClubTest extends MatcherTest {

    @Autowired
    private PersonClubFunctions personClubFunctions;

    @Autowired
    private MemberControllerApi memberControllerApi;

    @Autowired
    private ClubControllerApi clubControllerApi;

    private UUID clubId;

    @BeforeEach
    @Step
    @Title("Создание тестового клуба и добавление в него пользователя")
    void createClub() {
        clubId = UUID.fromString(clubControllerApi.createClub("1", "2", testTokenConfig.getAutotestToken()).getId());
        memberControllerApi.addMemberInClub(
                clubId,
                userConfigService.getUserByRole(UserRoleEnum.PERSONAL_ACCOUNT).getUuid(),
                testTokenConfig.getAutotestToken());
    }

    @Test
    @DisplayName("Проверка удаления клуба, из которого выходит пользователь")
    void checkDeleteClubInMatcher() {
        givenStep();

        var userInfo = userConfigService.getUserByRole(UserRoleEnum.PERSONAL_ACCOUNT);

        memberControllerApi.removeMemberFromClub(clubId, userInfo.getUuid(), testTokenConfig.getAutotestToken());

        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .until(() -> personClubFunctions.getUserClubs(userInfo.getUuid()).contains(clubId), is(false));

        thenStep();

        assertThat(
                "Клуб %s не входит в список клубов пользователя".formatted(clubId),
                personClubFunctions.getUserClubs(userInfo.getUuid()).contains(clubId),
                is(false));
    }

    @Step
    @Title("GIVEN: Получена информация о пользователе, у которого будет удален клуб")
    public void givenStep() {
    }

    @Step
    @Title("THEN: Список клубов пользователя успешно обновлен")
    public void thenStep() {
    }

    @AfterEach
    @Step
    @Title("Удаление ранее созданного тестового клуба")
    void deleteClub() {
        clubControllerApi.removeClub(clubId, testTokenConfig.getAutotestToken());
    }
}
