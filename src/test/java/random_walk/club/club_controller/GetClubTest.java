package random_walk.club.club_controller;

import club_service.graphql.model.Approvement;
import club_service.graphql.model.MemberRole;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.database.club.functions.ApprovementFunctions;
import random_walk.automation.database.club.functions.ClubFunctions;
import random_walk.automation.database.club.functions.MemberFunctions;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.club.ClubTest;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static random_walk.asserts.ErrorAsserts.checkGraphqlError;
import static random_walk.automation.util.ExceptionUtils.toGraphqlErrorResponse;

public class GetClubTest extends ClubTest {

    @Autowired
    private ClubFunctions clubFunctions;

    @Autowired
    private MemberFunctions memberFunctions;

    @Autowired
    private ApprovementFunctions approvementFunctions;

    private UUID clubId;

    @BeforeAll
    void createChat() {
        var inspectorId = userConfigService.getUserByRole(UserRoleEnum.PERSONAL_ACCOUNT).getUuid();

        clubId = UUID.fromString(
                clubControllerApi.createClub("GivenClub", "Клуб для тестов на получение", testTokenConfig.getAutotestToken())
                        .getId());

        memberControllerApi.addMemberInClub(clubId, inspectorId, testTokenConfig.getAutotestToken());

        memberControllerApi.changeMemberRole(clubId, inspectorId, MemberRole.INSPECTOR, testTokenConfig.getAutotestToken());
    }

    @Test
    @DisplayName("Получение информации о клубе")
    void getInfoAboutClub() {
        var clubInfo = clubControllerApi.getClub(clubId, testTokenConfig.getAutotestToken());

        var clubDb = clubFunctions.getById(clubId);

        var clubMembers = memberFunctions.getByClubId(clubId);

        var approvements = approvementFunctions.getByClubId(clubId);

        assertAll(
                "Проверяем полученную о клубе информацию",
                () -> assertThat(
                        "Кол-во аппруверов = кол-во админов + кол-во инспекторов",
                        clubInfo.getApproversNumber(),
                        equalTo(
                                clubInfo.getMembers()
                                        .stream()
                                        .filter(
                                                member -> member.getRole() == MemberRole.ADMIN
                                                        || member.getRole() == MemberRole.INSPECTOR)
                                        .toList()
                                        .size())),
                () -> assertThat(clubInfo.getName(), equalTo(clubDb.getName())),
                () -> assertThat(clubInfo.getDescription(), equalTo(clubDb.getDescription())),
                () -> clubInfo.getMembers().forEach(member -> {
                    var memberDb = clubMembers.stream()
                            .filter(member1 -> member1.getId().equals(UUID.fromString(member.getId())))
                            .findFirst()
                            .orElseThrow(NotFoundException::new);

                    assertAll(
                            "Параметры участника клуба",
                            () -> assertThat(member.getId(), equalTo(memberDb.getId().toString())),
                            () -> assertThat(member.getRole(), equalTo(memberDb.getRole())));
                }),
                () -> assertThat(
                        clubInfo.getApprovements(),
                        containsInAnyOrder(
                                approvements.stream()
                                        .map(approvement -> Approvement.builder().setId(approvement.getId().toString()).build())
                                        .toArray())));
    }

    @Test
    @DisplayName("Получение информации о несуществующем клубе")
    void getInfoAboutNonExistingClub() {
        var clubInfo = toGraphqlErrorResponse(
                () -> clubControllerApi.getClub(UUID.randomUUID(), testTokenConfig.getAutotestToken()));

        var errorCode = "UNAUTHORIZED";
        var errorMessage = "You are not become member of given club!";

        checkGraphqlError(clubInfo, errorCode, errorMessage);
    }

    @AfterAll
    void deleteChat() {
        clubControllerApi.removeClub(clubId, testTokenConfig.getAutotestToken());
    }
}
