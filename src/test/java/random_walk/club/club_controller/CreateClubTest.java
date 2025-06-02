package random_walk.club.club_controller;

import club_service.graphql.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.club.services.ClubControllerApi;
import random_walk.automation.database.club.functions.ApprovementFunctions;
import random_walk.automation.database.club.functions.ClubFunctions;
import random_walk.automation.database.club.functions.MemberFunctions;
import random_walk.automation.domain.User;
import random_walk.automation.util.ApprovementConverterUtils;
import random_walk.club.ClubTest;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static random_walk.asserts.ErrorAsserts.checkGraphqlError;
import static random_walk.automation.domain.enums.UserRoleEnum.FIFTH_TEST_USER;
import static random_walk.automation.util.ExceptionUtils.toGraphqlErrorResponse;

public class CreateClubTest extends ClubTest {

    @Autowired
    private ClubFunctions clubFunctions;

    @Autowired
    private MemberFunctions memberFunctions;

    @Autowired
    private ClubControllerApi clubControllerApi;

    @Autowired
    private ApprovementFunctions approvementFunctions;

    private String firstClubId;

    private String secondClubId;

    private String thirdClubId;

    private User user;

    private String userToken;

    @Test
    @DisplayName("Проверка создания клуба пользователем")
    void createClub() {
        givenStep();

        var createdClub = clubControllerApi.createClub("Клуб для создания", "Клуб", userToken);
        firstClubId = createdClub.getId();

        var clubDb = clubFunctions.getById(UUID.fromString(firstClubId));
        var clubMembers = memberFunctions.getByClubId(UUID.fromString(firstClubId));

        thenStep(createdClub, clubMembers, clubDb, user.getUuid());
    }

    @Step
    @Title("GIVEN: Получены данные пользователя, который будет создавать чат")
    void givenStep() {
        user = userConfigService.getUserByRole(FIFTH_TEST_USER);
        userToken = userConfigService.getAccessToken(FIFTH_TEST_USER);
    }

    @Step
    @Title("THEN: Клуб с ожидаемыми параметрами успешно создан")
    void thenStep(Club createdClub,
                  List<random_walk.automation.database.club.entities.Member> clubMembers,
                  random_walk.automation.database.club.entities.Club clubDb,
                  UUID userId) {
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
        var userAccessToken = userConfigService.getAccessToken(FIFTH_TEST_USER);

        firstClubId = clubControllerApi.createClub("Первый клуб", "Первое описание", userAccessToken).getId();
        secondClubId = clubControllerApi.createClub("Второй клуб", "Второе описание", userAccessToken).getId();
        thirdClubId = clubControllerApi.createClub("Третий клуб", "Третье описание", userAccessToken).getId();

        var error = toGraphqlErrorResponse(
                () -> clubControllerApi.createClub("Несозданный клуб", "Несозданное описание", userAccessToken));

        var errorCode = "BAD_REQUEST";
        var errorMessage = "You are reached maximum count of clubs!";

        checkGraphqlError(error, errorCode, errorMessage);
    }

    @Test
    @DisplayName("Создание клуба с тестами")
    void createClubWithTest() {
        List<QuestionInput> questions = List.of(
                QuestionInput.builder()
                        .setText("Тест вопрос")
                        .setAnswerType(AnswerType.SINGLE)
                        .setAnswerOptions(List.of("1", "2"))
                        .setCorrectOptionNumbers(List.of(0))
                        .build());
        var createdClub = clubControllerApi.createClubWithFormApprovement(
                "Клуб с тестом",
                "Клуб с тестом на вступление",
                FormInput.builder().setQuestions(questions).build(),
                userToken);
        firstClubId = createdClub.getId();

        var approvements = approvementFunctions.getByClubId(UUID.fromString(firstClubId));
        var clubDb = clubFunctions.getById(UUID.fromString(firstClubId));

        var approvementData = ApprovementConverterUtils.getFormApprovementData(approvements.get(0).getData());
        assertAll(
                "Проверяем создание клуба с тестами",
                () -> assertThat(createdClub.getName(), equalTo(clubDb.getName())),
                () -> assertThat(createdClub.getDescription(), equalTo(clubDb.getDescription())),
                () -> assertThat(approvements.size(), equalTo(1)),
                () -> assertThat(approvementData.getType(), equalTo("form_approvement_data")),
                () -> assertAll(
                        () -> assertThat(
                                approvementData.getQuestions().get(0).getAnswerOptions(),
                                equalTo(questions.get(0).getAnswerOptions())),
                        () -> assertThat(approvementData.getQuestions().get(0).getText(), equalTo(questions.get(0).getText())),
                        () -> assertThat(
                                approvementData.getQuestions().get(0).getAnswerType(),
                                equalTo(questions.get(0).getAnswerType())),
                        () -> assertThat(
                                approvementData.getQuestions().get(0).getCorrectOptionNumbers(),
                                equalTo(questions.get(0).getCorrectOptionNumbers()))));
    }

    @Test
    @DisplayName("Создание клуба с подтверждением от админов")
    void createClubWithApproversConfirm() {
        var requiredApprovers = 1;
        var approversToNotify = 2;
        var createdClub = clubControllerApi.createClubWithMemberConfirm(
                "Клуб с тестом",
                "Клуб с тестом на вступление",
                requiredApprovers,
                approversToNotify,
                userToken);
        firstClubId = createdClub.getId();

        var approvements = approvementFunctions.getByClubId(UUID.fromString(firstClubId));
        var clubDb = clubFunctions.getById(UUID.fromString(firstClubId));

        var approvementData = ApprovementConverterUtils.getConfirmApprovementData(approvements.get(0).getData());
        assertAll(
                "Проверяем создание клуба с тестами",
                () -> assertThat(createdClub.getName(), equalTo(clubDb.getName())),
                () -> assertThat(createdClub.getDescription(), equalTo(clubDb.getDescription())),
                () -> assertThat(approvements.size(), equalTo(1)),
                () -> assertThat(approvementData.getType(), equalTo("members_confirm_approvement_data")),
                () -> assertAll(
                        () -> assertThat(approvementData.getApproversToNotifyCount(), equalTo(approversToNotify)),
                        () -> assertThat(approvementData.getRequiredConfirmationNumber(), equalTo(requiredApprovers))));
    }

    @AfterEach
    void deleteUnnecessaryClubs() {
        var userToken = userConfigService.getAccessToken(FIFTH_TEST_USER);
        try {
            clubControllerApi.removeClub(UUID.fromString(firstClubId), userToken);
        } catch (Exception ignored) {
        }
        try {
            clubControllerApi.removeClub(UUID.fromString(secondClubId), userToken);
        } catch (Exception ignored) {
        }
        try {
            clubControllerApi.removeClub(UUID.fromString(thirdClubId), userToken);
        } catch (Exception ignored) {
        }
    }
}
