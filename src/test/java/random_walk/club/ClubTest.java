package random_walk.club;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.BaseTest;
import random_walk.automation.api.club.services.ClubControllerApi;
import random_walk.automation.api.club.services.MemberControllerApi;
import random_walk.automation.domain.enums.UserRoleEnum;

import java.util.UUID;

@Tag("club-e2e")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClubTest extends BaseTest {

    @Autowired
    protected ClubControllerApi clubControllerApi;

    @Autowired
    protected MemberControllerApi memberControllerApi;

    protected UUID createdClubId;

    protected static final String NOT_PHOTO_URL = "aHR0cHM6Ly9saDMuZ29vZ2xldXNlcmNvbnRlbnQuY29tL2EvQUNnOG9jSkxZRW5zVzhvcnFyUm5oSDI3bi1HUGh2LXNabHM5UkpmQVJEeENmQ2FOejRjWGlnPXM5Ni1j";

    @BeforeAll
    public void createTestClub() {
        createdClubId = UUID.fromString(
                clubControllerApi.createClub("AutotestClub", "Автотестовый создаваемый клуб", testTokenConfig.getToken())
                        .getId());
        memberControllerApi.addMemberInClub(
                createdClubId,
                userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER).getUuid(),
                testTokenConfig.getToken());
    }

    @AfterAll
    public void deleteTestClub() {
        clubControllerApi.removeClub(createdClubId, testTokenConfig.getToken());
    }
}
