package random_walk.club.club_controller;

import club_service.graphql.model.MemberRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.club.services.ClubControllerApi;
import random_walk.automation.database.club.functions.ClubFunctions;
import random_walk.automation.database.club.functions.MemberFunctions;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.club.ClubTest;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static random_walk.asserts.ErrorAsserts.checkGraphqlError;
import static random_walk.automation.util.ExceptionUtils.toGraphqlErrorResponse;

public class CreateClubTest extends ClubTest {

    @Autowired
    private ClubFunctions clubFunctions;

    @Autowired
    private MemberFunctions memberFunctions;

    @Autowired
    private ClubControllerApi clubControllerApi;

    private String firstClubId;

    private String secondClubId;

    @Test
    @DisplayName("Проверка создания клуба пользователем")
    void createClub() {
        var userId = userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER).getUuid();

        var createdClub = clubControllerApi.createClub("Клуб для создания", "Клуб", testTokenConfig.getAutotestToken());
        firstClubId = createdClub.getId();

        var clubDb = clubFunctions.getById(UUID.fromString(firstClubId));
        var clubMembers = memberFunctions.getByClubId(UUID.fromString(firstClubId));

        assertAll(
                "Проверка корректности создания клуба",
                () -> assertThat(createdClub.getName(), equalTo(clubDb.getName())),
                () -> assertThat(createdClub.getDescription(), equalTo(clubDb.getDescription())),
                () -> assertThat(clubMembers.size(), equalTo(1)),
                () -> assertThat(clubMembers.get(0).getId(), equalTo(userId)),
                () -> assertThat(clubMembers.get(0).getRole(), equalTo(MemberRole.ADMIN)));

    }

    @Test
    @DisplayName("Попытка создать количество клубов, превышающее допустимое для пользователя")
    void createClubAfterReachingMaxCountOfClubs() {
        firstClubId = clubControllerApi.createClub("Первый клуб", "Первое описание", testTokenConfig.getToken()).getId();
        secondClubId = clubControllerApi.createClub("Второй клуб", "Второе описание", testTokenConfig.getToken()).getId();

        var error = toGraphqlErrorResponse(
                () -> clubControllerApi.createClub("Несозданный клуб", "Несозданное описание", testTokenConfig.getToken()));

        var errorCode = "BAD_REQUEST";
        var errorMessage = "You are reached maximum count of clubs!";

        checkGraphqlError(error, errorCode, errorMessage);
    }

    @AfterEach
    void deleteUnnecessaryClubs() {
        try {
            clubControllerApi.removeClub(UUID.fromString(firstClubId), testTokenConfig.getToken());
        } catch (Exception ignored) {
        }
        try {
            clubControllerApi.removeClub(UUID.fromString(secondClubId), testTokenConfig.getToken());
        } catch (Exception ignored) {
        }
    }
}
