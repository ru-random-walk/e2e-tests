package random_walk.club.member_controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.club.services.ClubControllerApi;
import random_walk.automation.database.club.entities.prkeys.MemberPK;
import random_walk.automation.database.club.functions.MemberFunctions;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.club.ClubTest;
import ru.testit.annotations.Title;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static random_walk.asserts.ErrorAsserts.checkGraphqlError;
import static random_walk.automation.domain.enums.UserRoleEnum.*;
import static random_walk.automation.util.ExceptionUtils.toGraphqlErrorResponse;

public class RemoveMemberTest extends ClubTest {

    @Autowired
    private MemberFunctions memberFunctions;

    @Autowired
    private ClubControllerApi clubControllerApi;

    private UUID newClubId;

    private String adminToken;

    @Title("Создание тестового клуба для проверки удаления пользователей")
    @BeforeAll
    void createClub() {
        adminToken = userConfigService.getUserByRole(FOURTH_TEST_USER).getAccessToken();
        var newClub = clubControllerApi.createClub("test", "testClub", adminToken);
        newClubId = UUID.fromString(newClub.getId());
        var thirdUserId = userConfigService.getUserByRole(THIRD_TEST_USER).getUuid();
        memberControllerApi.addMemberInClub(newClubId, thirdUserId, adminToken);
        memberControllerApi.addMemberInClub(newClubId, userConfigService.getUserByRole(SECOND_TEST_USER).getUuid(), adminToken);
    }

    @Title("Добавление в клуб удаленного ранее пользователя")
    @BeforeEach
    void addUserInClub() {
        var userId = userConfigService.getUserByRole(UserRoleEnum.PERSONAL_ACCOUNT).getUuid();
        memberControllerApi.addMemberInClub(newClubId, userId, adminToken);
    }

    @Test
    @DisplayName("Проверка корректного удаления пользователя из клуба")
    void removeMemberFromClub() {
        var userId = userConfigService.getUserByRole(UserRoleEnum.PERSONAL_ACCOUNT).getUuid();

        var removedUser = memberControllerApi.removeMemberFromClub(newClubId, userId, adminToken);

        assertAll(
                "Проверяем успешное удаление пользователя из клуба",
                () -> assertThat("Из клуба был удален ожидаемый пользователь", removedUser, equalTo(userId.toString())),
                () -> assertThat(
                        "Пользователь удален из базы данных",
                        memberFunctions.getClubMember(new MemberPK().setId(userId).setClubId(newClubId)),
                        nullValue()),
                () -> assertThat(
                        "В ответе метода получения клуба пользователь не отображается",
                        clubControllerApi.getClub(newClubId, adminToken)
                                .getMembers()
                                .stream()
                                .anyMatch(user -> user.getId().equals(userId.toString())),
                        is(false)));
    }

    @Test
    @DisplayName("Проверка корректного выхода пользователя из группы")
    void selfRemoveMemberFromClub() {
        var user = userConfigService.getUserByRole(SECOND_TEST_USER);

        var removedUser = memberControllerApi.removeMemberFromClub(newClubId, user.getUuid(), user.getAccessToken());

        assertAll(
                "Проверяем успешное удаление пользователя из клуба",
                () -> assertThat("Из клуба был удален ожидаемый пользователь", removedUser, equalTo(user.getUuid().toString())),
                () -> assertThat(
                        "Пользователь удален из базы данных",
                        memberFunctions.getClubMember(new MemberPK().setId(user.getUuid()).setClubId(newClubId)),
                        nullValue()),
                () -> assertThat(
                        "В ответе метода получения клуба пользователь не отображается",
                        clubControllerApi.getClub(newClubId, adminToken)
                                .getMembers()
                                .stream()
                                .anyMatch(user1 -> user1.getId().equals(user.getUuid().toString())),
                        is(false)));
    }

    @Test
    @DisplayName("Проверка повторного удаления пользователя")
    void removeAlreadyRemovedMemberFromClub() {
        var userId = userConfigService.getUserByRole(UserRoleEnum.PERSONAL_ACCOUNT).getUuid();

        memberControllerApi.removeMemberFromClub(newClubId, userId, adminToken);

        var removedUser = toGraphqlErrorResponse(() -> memberControllerApi.removeMemberFromClub(newClubId, userId, adminToken));

        var classification = "NOT_FOUND";
        var errorMessage = "Member not found in club %s".formatted(newClubId);

        checkGraphqlError(removedUser, classification, errorMessage);
    }

    @Test
    @DisplayName("Проверка удаления пользователя из несуществующего клуба")
    void removeMemberFromNonExistingClub() {
        var userId = userConfigService.getUserByRole(UserRoleEnum.PERSONAL_ACCOUNT).getUuid();

        var removeNonExistingUser = toGraphqlErrorResponse(
                () -> memberControllerApi.removeMemberFromClub(UUID.randomUUID(), userId, adminToken));

        var classification = "UNAUTHORIZED";
        var errorMessage = "You are not become member of given club!";

        checkGraphqlError(removeNonExistingUser, classification, errorMessage);
    }

    @Test
    @DisplayName("Проверка удаления пользователя из клуба участником, не являющимся админом")
    void removeMemberFromClubByNotClubAdmin() {
        System.out.println(memberFunctions.getByClubId(newClubId));
        var userId = userConfigService.getUserByRole(UserRoleEnum.PERSONAL_ACCOUNT).getUuid();

        var removeNonExistingUser = toGraphqlErrorResponse(
                () -> memberControllerApi.removeMemberFromClub(
                        newClubId,
                        userId,
                        userConfigService.getUserByRole(THIRD_TEST_USER).getAccessToken()));

        var classification = "UNAUTHORIZED";
        var errorMessage = "You are not authorized to access this club members!";

        checkGraphqlError(removeNonExistingUser, classification, errorMessage);
    }

    @Test
    @DisplayName("Проверка удаления админом самого себя из клуба, когда админ единственный")
    void removeAdminMemberByHimself() {
        var userId = userConfigService.getUserByRole(FOURTH_TEST_USER).getUuid();

        var changeMemberRoleResponse = toGraphqlErrorResponse(
                () -> memberControllerApi.removeMemberFromClub(newClubId, userId, adminToken));

        var errorCode = "BAD_REQUEST";
        var errorMessage = "You are single admin in given club and you are not allowed to removing yourself!";

        checkGraphqlError(changeMemberRoleResponse, errorCode, errorMessage);
    }

    @AfterAll
    @Title("Удаление созданного тестового клуба")
    void deleteClub() {
        clubControllerApi.removeClub(newClubId, adminToken);
    }
}
