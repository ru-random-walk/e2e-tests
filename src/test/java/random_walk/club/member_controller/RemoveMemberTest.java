package random_walk.club.member_controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.database.club.entities.prkeys.MemberPK;
import random_walk.automation.database.club.functions.MemberFunctions;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.club.ClubTest;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static random_walk.asserts.ErrorAsserts.checkGraphqlError;
import static random_walk.automation.util.ExceptionUtils.toGraphqlErrorResponse;

public class RemoveMemberTest extends ClubTest {

    @Autowired
    private MemberFunctions memberFunctions;

    @BeforeEach
    void addUserInClub() {
        var userId = userConfigService.getUserByRole(UserRoleEnum.PERSONAL_ACCOUNT).getUuid();
        var autotestUserId = userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER).getUuid();

        memberControllerApi.addMemberInClub(createdClubId, userId, testTokenConfig.getToken());
        memberControllerApi.addMemberInClub(createdClubId, autotestUserId, testTokenConfig.getToken());
    }

    @Test
    @DisplayName("Проверка корректного удаления пользователя из клуба")
    void removeMemberFromClub() {
        var userId = userConfigService.getUserByRole(UserRoleEnum.PERSONAL_ACCOUNT).getUuid();

        var removedUser = memberControllerApi.removeMemberFromClub(createdClubId, userId, testTokenConfig.getToken());

        assertAll(
                "Проверяем успешное удаление пользователя из клуба",
                () -> assertThat("Из клуба был удален ожидаемый пользователь", removedUser, equalTo(userId.toString())),
                () -> assertThat(
                        "Пользователь удален из базы данных",
                        memberFunctions.getClubMember(new MemberPK().setId(userId).setClubId(createdClubId)),
                        nullValue()),
                () -> assertThat(
                        "В ответе метода получения клуба пользователь не отображается",
                        clubControllerApi.getClub(createdClubId, testTokenConfig.getToken())
                                .getMembers()
                                .stream()
                                .anyMatch(user -> user.getId().equals(userId.toString())),
                        is(false)));
    }

    @Test
    @DisplayName("Проверка повторного удаления пользователя")
    void removeAlreadyRemovedMemberFromClub() {
        var userId = userConfigService.getUserByRole(UserRoleEnum.PERSONAL_ACCOUNT).getUuid();

        memberControllerApi.removeMemberFromClub(createdClubId, userId, testTokenConfig.getToken());

        var removedUser = toGraphqlErrorResponse(
                () -> memberControllerApi.removeMemberFromClub(createdClubId, userId, testTokenConfig.getToken()));

        var classification = "NOT_FOUND";
        var errorMessage = "Member not found in club %s".formatted(createdClubId);

        checkGraphqlError(removedUser, classification, errorMessage);
    }

    @Test
    @DisplayName("Проверка удаления пользователя из несуществующего клуба")
    void removeMemberFromNonExistingClub() {
        var userId = userConfigService.getUserByRole(UserRoleEnum.PERSONAL_ACCOUNT).getUuid();

        var removeNonExistingUser = toGraphqlErrorResponse(
                () -> memberControllerApi.removeMemberFromClub(UUID.randomUUID(), userId, testTokenConfig.getToken()));

        var classification = "UNAUTHORIZED";
        var errorMessage = "You are not become member of given club!";

        checkGraphqlError(removeNonExistingUser, classification, errorMessage);
    }

    @Test
    @DisplayName("Проверка удаления пользователя из клуба участником, не являющимся админом")
    void removeMemberFromClubByNotClubAdmin() {
        var userId = userConfigService.getUserByRole(UserRoleEnum.PERSONAL_ACCOUNT).getUuid();

        var removeNonExistingUser = toGraphqlErrorResponse(
                () -> memberControllerApi.removeMemberFromClub(createdClubId, userId, testTokenConfig.getAutotestToken()));

        var classification = "UNAUTHORIZED";
        var errorMessage = "You are not authorized to access this club members!";

        checkGraphqlError(removeNonExistingUser, classification, errorMessage);
    }

    @Test
    @Disabled("Тут аналогично")
    @DisplayName("Проверка удаления админом самого себя из клуба, когда админ единственный")
    void removeAdminMemberByHimself() {
        var userId = userConfigService.getUserByRole(UserRoleEnum.TEST_USER).getUuid();

        var changeMemberRoleResponse = memberControllerApi
                .removeMemberFromClub(createdClubId, userId, testTokenConfig.getToken());

        System.out.println(memberFunctions.getByClubId(createdClubId));
    }

}
