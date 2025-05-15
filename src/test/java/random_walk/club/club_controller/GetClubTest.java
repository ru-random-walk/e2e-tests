package random_walk.club.club_controller;

import club_service.graphql.model.MemberRole;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.database.club.functions.ClubFunctions;
import random_walk.automation.database.club.functions.MemberFunctions;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.club.ClubTest;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

public class GetClubTest extends ClubTest {

    @Autowired
    private ClubFunctions clubFunctions;

    @Autowired
    private MemberFunctions memberFunctions;

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
                                        .size())));
    }

    @AfterAll
    void deleteChat() {
        clubControllerApi.removeClub(clubId, testTokenConfig.getAutotestToken());
    }
}
