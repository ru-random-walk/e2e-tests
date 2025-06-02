package random_walk.club.member_controller;

import club_service.graphql.model.MemberRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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

public class ChangeMemberRoleTest extends ClubTest {

    @Autowired
    private MemberFunctions memberFunctions;

    @BeforeEach
    void addUserInClub() {
        var userId = userConfigService.getUserByRole(UserRoleEnum.PERSONAL_ACCOUNT).getUuid();

        memberControllerApi
                .addMemberInClub(createdClubId, userId, userConfigService.getUserByRole(FOURTH_TEST_USER).getAccessToken());
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(value = MemberRole.class)
    @DisplayName("Проверка корректной смены роли участника клуба на")
    void changeMemberRoleInClub(MemberRole memberRole) {
        var userId = userConfigService.getUserByRole(UserRoleEnum.PERSONAL_ACCOUNT).getUuid();

        var changeMemberRoleResponse = memberControllerApi.changeMemberRole(
                createdClubId,
                userId,
                memberRole,
                userConfigService.getUserByRole(FOURTH_TEST_USER).getAccessToken());

        var memberDb = memberFunctions.getClubMember(new MemberPK().setId(userId).setClubId(createdClubId));

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
                        clubControllerApi
                                .getClub(createdClubId, userConfigService.getUserByRole(FOURTH_TEST_USER).getAccessToken())
                                .getMembers()
                                .stream()
                                .anyMatch(user -> user.getId().equals(userId.toString()) && user.getRole().equals(memberRole)),
                        is(true)));
    }

    @Test
    @Disabled("Должна быть ошибка, зайду к Максу")
    @DisplayName("Проверка смены роли единственного админа на обычного пользователя")
    void changeAdminRoleToUser() {
        var userId = userConfigService.getUserByRole(UserRoleEnum.TEST_USER).getUuid();

        var changeMemberRoleResponse = memberControllerApi.changeMemberRole(
                createdClubId,
                userId,
                MemberRole.USER,
                userConfigService.getUserByRole(FOURTH_TEST_USER).getAccessToken());

        System.out.println(memberFunctions.getByClubId(createdClubId));
    }

    @Test
    @DisplayName("Проверка смены роли в клубе пользователю участником, не являющимся админом")
    void changeMemberRoleByNotClubAdmin() {
        var userId = userConfigService.getUserByRole(UserRoleEnum.PERSONAL_ACCOUNT).getUuid();

        var changeMemberRoleResponse = toGraphqlErrorResponse(
                () -> memberControllerApi
                        .changeMemberRole(createdClubId, userId, MemberRole.USER, testTokenConfig.getAutotestToken()));

        var classification = "UNAUTHORIZED";
        var errorMessage = "You are not authorized to access this club members!";

        checkGraphqlError(changeMemberRoleResponse, classification, errorMessage);
    }

    @Test
    @DisplayName("Проверка смены роли несуществующему пользователю")
    void changeMemberRoleToNonExistingMember() {
        var changeMemberRoleResponse = toGraphqlErrorResponse(
                () -> memberControllerApi
                        .changeMemberRole(createdClubId, UUID.randomUUID(), MemberRole.USER, testTokenConfig.getToken()));

        var classification = "NOT_FOUND";
        var errorMessage = "Member not found in club %s".formatted(createdClubId);

        checkGraphqlError(changeMemberRoleResponse, classification, errorMessage);
    }
}
