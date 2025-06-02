package random_walk.club.member_controller;

import club_service.graphql.model.MemberRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.database.club.entities.prkeys.MemberPK;
import random_walk.automation.database.club.functions.MemberFunctions;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.club.ClubTest;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static random_walk.asserts.ErrorAsserts.checkGraphqlError;
import static random_walk.automation.domain.enums.UserRoleEnum.FOURTH_TEST_USER;
import static random_walk.automation.util.ExceptionUtils.toGraphqlErrorResponse;

class AddMemberTest extends ClubTest {

    @Autowired
    private MemberFunctions memberFunctions;

    @Test
    @DisplayName("Проверка добавления участника в клуб")
    void checkAddNewMemberInClub() {
        var userInfo = userConfigService.getUserByRole(UserRoleEnum.PERSONAL_ACCOUNT);

        var addMemberResponse = memberControllerApi
                .addMemberInClub(createdClubId, userInfo.getUuid(), userConfigService.getAccessToken(FOURTH_TEST_USER));

        var clubMemberDb = memberFunctions.getClubMember(new MemberPK().setId(userInfo.getUuid()).setClubId(createdClubId));

        assertAll(
                "Проверяем успешное добавление участника в клуб",
                () -> assertThat(
                        "В клуб добавлен корректный участник",
                        addMemberResponse.getId(),
                        equalTo(clubMemberDb.getId().toString())),
                () -> assertThat("Роль участника корректна", addMemberResponse.getRole(), equalTo(clubMemberDb.getRole())),
                () -> assertThat(
                        "Участник добавлен в группу с дефолтной ролью обычного пользователя",
                        addMemberResponse.getRole(),
                        equalTo(MemberRole.USER)),
                () -> assertThat(
                        "Пользователь отображается в методе получения информации о клубе",
                        clubControllerApi.getClub(createdClubId, userConfigService.getAccessToken(FOURTH_TEST_USER))
                                .getMembers()
                                .stream()
                                .anyMatch(user -> user.getId().equals(userInfo.getUuid().toString())),
                        is(true)));

    }

    @Test
    @DisplayName("Проверка добавления несуществующего участника")
    void addNonExistingMemberInClub() {
        var addMemberResponse = toGraphqlErrorResponse(
                () -> memberControllerApi.addMemberInClub(createdClubId, UUID.randomUUID(), testTokenConfig.getToken()));

        var classification = "INTERNAL_ERROR";
        var errorMessage = "Unknown internal error!";

        checkGraphqlError(addMemberResponse, classification, errorMessage);
    }

    @Test
    @DisplayName("Проверка добавления участника в клуб пользователем, не являющимся админом")
    void addMemberInClubNotByClubAdmin() {
        var userInfo = userConfigService.getUserByRole(UserRoleEnum.PERSONAL_ACCOUNT);

        var addMemberResponse = toGraphqlErrorResponse(
                () -> memberControllerApi.addMemberInClub(createdClubId, userInfo.getUuid(), testTokenConfig.getAutotestToken()));

        var classification = "UNAUTHORIZED";
        var errorMessage = "You are not authorized to access this club members!";

        checkGraphqlError(addMemberResponse, classification, errorMessage);
    }

    @Test
    @DisplayName("Проверка добавления пользователя в несуществующий клуб")
    void addMemberInNonExistingClub() {
        var userInfo = userConfigService.getUserByRole(UserRoleEnum.PERSONAL_ACCOUNT);

        var addMemberResponse = toGraphqlErrorResponse(
                () -> memberControllerApi
                        .addMemberInClub(UUID.randomUUID(), userInfo.getUuid(), testTokenConfig.getAutotestToken()));

        var classification = "UNAUTHORIZED";
        var errorMessage = "You are not become member of given club!";

        checkGraphqlError(addMemberResponse, classification, errorMessage);
    }
}
