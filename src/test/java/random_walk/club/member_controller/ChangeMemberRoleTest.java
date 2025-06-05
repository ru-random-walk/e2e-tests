package random_walk.club.member_controller;

import club_service.graphql.model.MemberRole;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.database.club.entities.prkeys.MemberPK;
import random_walk.automation.database.club.functions.MemberFunctions;
import random_walk.club.ClubTest;
import ru.testit.annotations.Title;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static random_walk.asserts.ErrorAsserts.checkGraphqlError;
import static random_walk.automation.domain.enums.UserRoleEnum.FOURTH_TEST_USER;
import static random_walk.automation.domain.enums.UserRoleEnum.THIRD_TEST_USER;
import static random_walk.automation.util.ExceptionUtils.toGraphqlErrorResponse;

public class ChangeMemberRoleTest extends ClubTest {

    @Autowired
    private MemberFunctions memberFunctions;

    private String adminToken;

    private UUID newClubId;

    @Title("Создание тестового клуба для проверки удаления пользователей")
    @BeforeAll
    void createClub() {
        adminToken = userConfigService.getUserByRole(FOURTH_TEST_USER).getAccessToken();
        var newClub = clubControllerApi.createClub("test", "testClub", adminToken);
        newClubId = UUID.fromString(newClub.getId());
        var thirdUserId = userConfigService.getUserByRole(THIRD_TEST_USER).getUuid();
        memberControllerApi.addMemberInClub(newClubId, thirdUserId, adminToken);
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(value = MemberRole.class)
    @DisplayName("Проверка корректной смены роли участника клуба на")
    void changeMemberRoleInClub(MemberRole memberRole) {
        var userId = userConfigService.getUserByRole(THIRD_TEST_USER).getUuid();

        var changeMemberRoleResponse = memberControllerApi.changeMemberRole(newClubId, userId, memberRole, adminToken);

        var memberDb = memberFunctions.getClubMember(new MemberPK().setId(userId).setClubId(newClubId));

        assertAll(
                "Проверяем корректность смены роли участника",
                () -> assertThat(
                        "Был изменен ожидаемый участник клуба",
                        changeMemberRoleResponse.getId(),
                        equalTo(userId.toString())),
                () -> assertThat(
                        "Измененный статус соответствует ожидаемому",
                        changeMemberRoleResponse.getRole(),
                        equalTo(memberDb.getRole())),
                () -> assertThat(
                        "Статус был изменен на переданный в запросе",
                        changeMemberRoleResponse.getRole(),
                        equalTo(memberRole)),
                () -> assertThat(
                        "Статус пользователя изменен в ответе метода получения информации о клубе",
                        clubControllerApi.getClub(newClubId, adminToken)
                                .getMembers()
                                .stream()
                                .anyMatch(user -> user.getId().equals(userId.toString()) && user.getRole().equals(memberRole)),
                        is(true)));
    }

    @Test
    // @Disabled("Должна быть ошибка, зайду к Максу")
    @DisplayName("Проверка смены роли единственного админа на обычного пользователя")
    void changeAdminRoleToUser() {
        var userId = userConfigService.getUserByRole(FOURTH_TEST_USER).getUuid();

        var changeMemberRoleResponse = toGraphqlErrorResponse(
                () -> memberControllerApi.changeMemberRole(
                        newClubId,
                        userId,
                        MemberRole.USER,
                        userConfigService.getUserByRole(FOURTH_TEST_USER).getAccessToken()));

        var errorMessage = "You are single admin in given club and you are not allowed to removing yourself!";
        var errorCode = "BAD_REQUEST";

        checkGraphqlError(changeMemberRoleResponse, errorCode, errorMessage);
    }

    @Test
    @DisplayName("Проверка смены роли в клубе пользователю участником, не являющимся админом")
    void changeMemberRoleByNotClubAdmin() {
        var userId = userConfigService.getUserByRole(THIRD_TEST_USER).getUuid();

        var changeMemberRoleResponse = toGraphqlErrorResponse(
                () -> memberControllerApi.changeMemberRole(
                        newClubId,
                        userId,
                        MemberRole.USER,
                        userConfigService.getUserByRole(THIRD_TEST_USER).getAccessToken()));

        var classification = "UNAUTHORIZED";
        var errorMessage = "You are not authorized to access this club members!";

        checkGraphqlError(changeMemberRoleResponse, classification, errorMessage);
    }

    @Test
    @DisplayName("Проверка смены роли несуществующему пользователю")
    void changeMemberRoleToNonExistingMember() {
        var changeMemberRoleResponse = toGraphqlErrorResponse(
                () -> memberControllerApi.changeMemberRole(newClubId, UUID.randomUUID(), MemberRole.USER, adminToken));

        var classification = "NOT_FOUND";
        var errorMessage = "Member not found in club %s".formatted(newClubId);

        checkGraphqlError(changeMemberRoleResponse, classification, errorMessage);
    }

    @AfterAll
    @Title("Удаление созданного тестового клуба")
    void deleteClub() {
        clubControllerApi.removeClub(newClubId, adminToken);
    }
}
