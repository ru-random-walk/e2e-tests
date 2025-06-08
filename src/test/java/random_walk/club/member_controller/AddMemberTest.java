package random_walk.club.member_controller;

import club_service.graphql.model.MemberRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.database.club.entities.prkeys.MemberPK;
import random_walk.automation.database.club.functions.MemberFunctions;
import random_walk.automation.domain.User;
import random_walk.automation.domain.enums.ClubRole;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.club.ClubTest;
import ru.testit.annotations.DisplayName;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static random_walk.asserts.ErrorAsserts.checkGraphqlError;
import static random_walk.automation.domain.enums.UserRoleEnum.*;
import static random_walk.automation.util.ExceptionUtils.toGraphqlErrorResponse;

class AddMemberTest extends ClubTest {

    @Autowired
    private MemberFunctions memberFunctions;

    private User userInfo;

    @Test
    @DisplayName("Проверка добавления участника в клуб")
    void checkAddNewMemberInClub() {
        givenStep();

        var addMemberResponse = memberControllerApi.addMemberInClub(
                createdClubId,
                userInfo.getUuid(),
                userConfigService.getUserByRole(FOURTH_TEST_USER).getAccessToken());

        var clubMemberDb = memberFunctions.getClubMember(new MemberPK().setId(userInfo.getUuid()).setClubId(createdClubId));

        thenStep();
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
                        clubControllerApi
                                .getClub(createdClubId, userConfigService.getUserByRole(FOURTH_TEST_USER).getAccessToken())
                                .getMembers()
                                .stream()
                                .anyMatch(user -> user.getId().equals(userInfo.getUuid().toString())),
                        is(true)));

    }

    @Test
    @DisplayName("Проверка добавления несуществующего участника")
    void addNonExistingMemberInClub() {
        givenStep();

        var addMemberResponse = toGraphqlErrorResponse(
                () -> memberControllerApi.addMemberInClub(
                        createdClubId,
                        UUID.randomUUID(),
                        userConfigService.getUserByRole(FOURTH_TEST_USER).getAccessToken()));

        var classification = "INTERNAL_ERROR";
        var errorMessage = "Unknown internal error!";

        checkGraphqlError(addMemberResponse, classification, errorMessage);
    }

    @Test
    @DisplayName("Проверка добавления участника в клуб пользователем, не являющимся админом")
    void addMemberInClubNotByClubAdmin() {
        givenStep();

        var addMemberResponse = toGraphqlErrorResponse(
                () -> memberControllerApi.addMemberInClub(createdClubId, userInfo.getUuid(), testTokenConfig.getAutotestToken()));

        memberControllerApi.addMemberInClub(
                clubConfigService.getClubByRole(ClubRole.DEFAULT_CLUB).getId(),
                userConfigService.getUserByRole(FIRST_TEST_USER).getUuid(),
                testTokenConfig.getToken());
        memberControllerApi.addMemberInClub(
                clubConfigService.getClubByRole(ClubRole.DEFAULT_CLUB).getId(),
                userConfigService.getUserByRole(SECOND_TEST_USER).getUuid(),
                testTokenConfig.getToken());
        var classification = "UNAUTHORIZED";
        var errorMessage = "You are not authorized to access this club members!";

        checkGraphqlError(addMemberResponse, classification, errorMessage);
    }

    @Test
    @DisplayName("Проверка добавления пользователя в несуществующий клуб")
    void addMemberInNonExistingClub() {
        givenStep();

        var addMemberResponse = toGraphqlErrorResponse(
                () -> memberControllerApi
                        .addMemberInClub(UUID.randomUUID(), userInfo.getUuid(), testTokenConfig.getAutotestToken()));

        var classification = "UNAUTHORIZED";
        var errorMessage = "You are not become member of given club!";

        checkGraphqlError(addMemberResponse, classification, errorMessage);
    }

    @Step
    @Title("GIVEN: Получены данные пользователя, который будет добавлен в клуб")
    public void givenStep() {
        userInfo = userConfigService.getUserByRole(UserRoleEnum.PERSONAL_ACCOUNT);
    }

    @Step
    @Title("THEN: Пользователь успешно добавлен в клуб")
    public void thenStep() {
    }
}
